# 🏺 Kutira-Kushala (कुटीर-कुशला)
### Micro-Factory Showcase App — Android Internship Project

---

## 📋 Project Overview

**Kutira-Kushala** transforms invisible cottage industries into discoverable, professional "Micro-Factory" profiles. Home-based producers (basket weavers, agarbatti rollers, papad makers) can showcase their production capacity to bulk buyers — turning a home kitchen into a trusted business entity.

---

## 🏗️ Architecture & Tech Stack

| Component | Technology |
|-----------|-----------|
| Platform | Android (Java, minSdk 24) |
| Database | Firebase Firestore |
| Image Storage | Firebase Storage |
| UI Components | Material Design 3, CardViews, RecyclerView |
| Image Loading | Glide |
| Build | Gradle with ViewBinding |

---

## 📁 Project Structure

```
app/src/main/java/com/kutirakushala/
├── activities/
│   ├── SplashActivity.java          ← Branded launch screen
│   ├── MainActivity.java            ← Home: business grid + category filter
│   ├── BusinessDetailActivity.java  ← Full profile, capacity meter, product catalog
│   ├── AddBusinessActivity.java     ← Register new cottage business
│   ├── AddProductActivity.java      ← Add product to catalog
│   └── SearchActivity.java          ← Real-time search across businesses
├── adapters/
│   ├── BusinessAdapter.java         ← RecyclerView for business cards
│   └── ProductAdapter.java          ← RecyclerView for product cards
├── models/
│   ├── Business.java                ← Business data model (Firestore POJO)
│   └── Product.java                 ← Product data model (Firestore POJO)
└── utils/
    └── FirebaseHelper.java          ← All Firebase operations (singleton)
```

---

## 🚀 Setup Instructions

### Step 1: Firebase Project Setup

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project: **"KutiraKushala"**
3. Add an **Android app** with package name: `com.kutirakushala`
4. Download `google-services.json` and place it in `app/` folder
5. Enable **Firestore Database** (start in test mode for development)
6. Enable **Firebase Storage** (start in test mode)

### Step 2: Firestore Indexes

In Firebase Console → Firestore → Indexes, create these **composite indexes**:

| Collection | Fields | Order |
|------------|--------|-------|
| `businesses` | `category` ASC, `createdAt` DESC | — |
| `products` | `businessId` ASC, `createdAt` DESC | — |

### Step 3: Firestore Security Rules (for production)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /businesses/{businessId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

### Step 4: Open in Android Studio

1. Open Android Studio → **Open Existing Project**
2. Select the `KutiraKushala` folder
3. Wait for Gradle sync to complete
4. Click **Run ▶** on a device or emulator (API 24+)

---

## ✅ Success Criteria Checklist

| Criterion | Implementation |
|-----------|---------------|
| ✅ Capacity Meter easily updatable | `btnUpdateCapacity` opens dialog → updates Firestore in 1 tap |
| ✅ Filter by Product Category | ChipGroup in MainActivity with All/Food/Craft/Textile/Agriculture/Other |
| ✅ Clean UI focused on products | CardView grid + product catalog with photos, prices, capacity |
| ✅ Direct Connect button | Calls entrepreneur via `tel:` intent |
| ✅ Business Profile | Photo, skills, location, owner name |
| ✅ Product Catalog | Photo, bulk price, min order, daily capacity |
| ✅ Firebase Firestore | Real-time searchable directory |
| ✅ Image Storage | Firebase Storage for profile, workshop, product photos |

---

## 📱 App Screens

### 1. Splash Screen
- Saffron background with app logo and tagline
- 2-second delay → Main screen

### 2. Main Screen (Business Directory)
- Category filter chips: All / Food / Craft / Textile / Agriculture / Other
- 2-column grid of business cards
- Each card: profile photo, name, location, category badge, available units
- Search button (top-right) → Search screen
- FAB → Register new business

### 3. Business Detail Screen
- Workshop/banner photo header
- Profile card: photo, name, category, location, description
- **Capacity Meter**: progress bar + available units + accepting status
- **Update Capacity button**: opens dialog to set available units & toggle order acceptance
- **"Contact for Bulk Order"** green button → dials phone
- Product Catalog list with photos, prices, daily capacity
- Mini FAB → Add product

### 4. Register Business Screen
- Owner name, business name, category, location, phone
- Weekly production capacity (sets initial meter value)
- Optional: team photo + workshop photo upload
- Saves to Firestore + Firebase Storage

### 5. Add Product Screen
- Product name, description, bulk price, unit, min order, daily capacity
- Optional: product photo upload
- Saves as sub-document linked to business

### 6. Search Screen
- Real-time text search across business name, category, location, owner
- Results update as user types

---

## 🎨 Design Language

- **Primary color**: Saffron `#E07B39` — warmth, craft, Indian heritage
- **Background**: Cream `#FDF8F3` — clean, paper-like feel
- **Accent**: Forest green `#2D9E6B` — capacity, growth, availability
- **Typography**: Bold for business names, regular for details
- **Cards**: Rounded corners (12dp), soft elevation

---

## 🔧 Key Classes Reference

### FirebaseHelper (Singleton)
```java
// Get all businesses
FirebaseHelper.getInstance().getAllBusinesses(listener);

// Filter by category
FirebaseHelper.getInstance().getBusinessesByCategory("Food", listener);

// Update capacity meter (the key feature)
FirebaseHelper.getInstance().updateCapacityMeter(businessId, 150, true, listener);

// Add product
FirebaseHelper.getInstance().addProduct(product, listener);

// Upload image
FirebaseHelper.getInstance().uploadImage(uri, "products", listener);
```

### Capacity Meter Update Flow
```
User taps "Update" button
    → Dialog opens (EditText for units + Switch for accepting orders)
    → User sets values
    → FirebaseHelper.updateCapacityMeter() called
    → Only 2 fields updated in Firestore (efficient)
    → UI refreshes immediately
```

---

## 📊 Firestore Data Schema

### `businesses` collection
```json
{
  "ownerName": "Meena Devi",
  "businessName": "Devi Basket Works",
  "category": "Craft",
  "location": "Barabanki, UP",
  "phone": "9876543210",
  "description": "Traditional bamboo baskets since 1998",
  "profileImageUrl": "https://...",
  "workshopImageUrl": "https://...",
  "weeklyCapacity": 500,
  "availableUnits": 200,
  "isAcceptingOrders": true,
  "createdAt": 1703001600000
}
```

### `products` collection
```json
{
  "businessId": "abc123",
  "name": "Medium Storage Basket",
  "description": "Handwoven bamboo, 30cm diameter",
  "bulkPrice": 85.0,
  "unit": "piece",
  "minimumOrder": 50,
  "dailyCapacity": 80,
  "imageUrl": "https://...",
  "isAvailable": true,
  "createdAt": 1703001600000
}
```

---

## 🌟 Impact Goals

- **Rural Industrialization**: Cottage industries become discoverable to city buyers
- **Women's Empowerment**: Most are women-led; app gives them negotiating power
- **Atmanirbhar Bharat**: Local production of daily-use items gets market access

---

## 👨‍💻 Developed By

Internship Project — [Your Name]  
Guide: [Mentor Name]  
Institution: [College Name]

---

*"Every home is a factory. Every artisan is an entrepreneur."*
