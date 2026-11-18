const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_zivora_123";
const BOB_UID = "bob_zivora_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any ZIVORA collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("rideBookings").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("orders").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("addresses").get());
});

test("User profile: owner create, partial update, and cross-user isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Sharma",
      email: "alice@example.com",
      defaultAddress: "MG Road, Sector 14",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      defaultAddress: "Cyber Hub, Tower B",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject cross-user read/write
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      defaultAddress: "Hijacked Address",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Ride bookings: active vehicles succeed, BIKE is strictly rejected, terminal states lock", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  const activeVehicles = ["CAR", "TAXI", "AUTO", "E_RICKSHAW", "PICKUP", "LOADER"];
  for (let i = 0; i < activeVehicles.length; i++) {
    const vehicle = activeVehicles[i];
    await assertSucceeds(
      aliceDb.collection("users").doc(ALICE_UID).collection("rideBookings").doc(`ride_${i}`).set({
        userId: ALICE_UID,
        vehicleType: vehicle,
        pickupLocation: "Central Plaza",
        dropLocation: "City Airport",
        distanceKm: 8.5,
        fareAmount: 240,
        paymentMethod: "ZIVORA_PAY",
        driverName: "Rajesh Kumar",
        vehicleNumber: "DL-01-ZV-4821",
        otpCode: "4829",
        status: "CONFIRMED",
        createdAt: ts,
        updatedAt: ts,
      })
    );
  }

  // BIKE must be rejected by Firestore rules as well!
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("rideBookings").doc("ride_bike").set({
      userId: ALICE_UID,
      vehicleType: "BIKE",
      pickupLocation: "Central Plaza",
      dropLocation: "City Airport",
      distanceKm: 8.5,
      fareAmount: 75,
      paymentMethod: "ZIVORA_PAY",
      driverName: "Test Driver",
      vehicleNumber: "DL-01-BK-0001",
      otpCode: "1234",
      status: "CONFIRMED",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Valid transition: CONFIRMED -> IN_PROGRESS -> COMPLETED
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("rideBookings").doc("ride_0").update({
      status: "IN_PROGRESS",
      updatedAt: new Date(Date.now() - 2000),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("rideBookings").doc("ride_0").update({
      status: "COMPLETED",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Terminal state lock: COMPLETED cannot revert
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("rideBookings").doc("ride_0").update({
      status: "CANCELLED",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Cross-user list & get must fail
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("rideBookings").get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("rideBookings").get());
});

test("Grocery & Food orders: valid creation, transitions, and ghost field rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_1").set({
      userId: ALICE_UID,
      orderType: "GROCERY",
      storeName: "ZIVORA Fresh Mart",
      itemsSummary: "2x Organic Avocados, 1x A2 Milk",
      itemCount: 3,
      totalAmount: 380,
      deliveryAddress: "MG Road, Sector 14",
      deliverySlot: "Express 15 mins",
      paymentMethod: "UPI",
      status: "PLACED",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_2").set({
      userId: ALICE_UID,
      orderType: "FOOD",
      storeName: "Saffron Spice Kitchen",
      itemsSummary: "1x Paneer Tikka Bowl, 2x Garlic Naan",
      itemCount: 3,
      totalAmount: 495,
      deliveryAddress: "MG Road, Sector 14",
      deliverySlot: "25-30 mins",
      paymentMethod: "ZIVORA_PAY",
      status: "PLACED",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Reject ghost `id` field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_3").set({
      id: "ord_3",
      userId: ALICE_UID,
      orderType: "FOOD",
      storeName: "Saffron Spice Kitchen",
      itemsSummary: "1x Bowl",
      itemCount: 1,
      totalAmount: 250,
      deliveryAddress: "MG Road, Sector 14",
      deliverySlot: "25 mins",
      paymentMethod: "CASH",
      status: "PLACED",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Transition PLACED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_1").update({
      status: "PREPARING",
      updatedAt: new Date(Date.now() - 3000),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_1").update({
      status: "OUT_FOR_DELIVERY",
      updatedAt: new Date(Date.now() - 2000),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("orders").doc("ord_1").update({
      status: "DELIVERED",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Saved address CRUD
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("addresses").doc("addr_1").set({
      userId: ALICE_UID,
      label: "Home",
      fullAddress: "B-42, Green Park Residency",
      landmark: "Near Metro Gate 2",
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("addresses").get());
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("addresses").doc("addr_1").delete());
});
