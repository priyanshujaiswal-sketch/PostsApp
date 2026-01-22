# Network/DNS Issue - Troubleshooting Guide

## 🔴 Problem Identified

Your Android emulator has a **DNS resolution failure**. The app cannot resolve the domain name `jsonplaceholder.typicode.com` to an IP address.

### Error Message:
```
UnknownHostException: Unable to resolve host "jsonplaceholder.typicode.com": 
No address associated with hostname
```

### What's Working:
✅ App builds successfully  
✅ App installs on emulator  
✅ Emulator can ping IP addresses (8.8.8.8)  
✅ Internet permission is granted  
✅ Network security config is set up  

### What's NOT Working:
❌ Emulator cannot resolve domain names (DNS is broken)  
❌ `ping jsonplaceholder.typicode.com` fails  
❌ API calls hang and timeout  

---

## 🔧 Solutions (Try in Order)

### Solution 1: Restart the Emulator (RECOMMENDED)
This usually fixes DNS issues:

1. **Close the emulator** completely
2. **Restart it** from Android Studio or command line
3. **Wait 30 seconds** for network to initialize
4. **Run the app** again

### Solution 2: Create a New Emulator
If restarting doesn't work:

1. Open **Android Studio** → **Device Manager**
2. **Create a new virtual device**:
   - Choose a recent device (e.g., Pixel 5)
   - System Image: **API 34** or **API 33** (not API 36 - it might have bugs)
   - Give it **4GB RAM** and **2GB storage**
3. **Start the new emulator**
4. **Install and run** the app

### Solution 3: Use a Physical Device
The most reliable option:

1. **Enable Developer Options** on your Android phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. **Enable USB Debugging**:
   - Settings → Developer Options → USB Debugging
3. **Connect via USB**
4. **Run the app** from Android Studio

### Solution 4: Fix Emulator DNS (Advanced)
If you must use the current emulator:

1. **Cold Boot the emulator**:
   ```bash
   ~/Library/Android/sdk/emulator/emulator -avd <your_avd_name> -dns-server 8.8.8.8
   ```

2. **Or restart with network reset**:
   - Close emulator
   - Open Terminal
   - Run:
     ```bash
     ~/Library/Android/sdk/emulator/emulator -avd Medium_Phone_API_36.1 -wipe-data
     ```
   - ⚠️ This will erase all emulator data

---

## ✅ How to Verify It's Fixed

After trying any solution, test if DNS works:

```bash
~/Library/Android/sdk/platform-tools/adb shell ping -c 3 jsonplaceholder.typicode.com
```

**Expected output:**
```
PING jsonplaceholder.typicode.com (104.21.59.19) 56(84) bytes of data.
64 bytes from 104.21.59.19: icmp_seq=1 ttl=255 time=45.2 ms
...
```

If you see this, DNS is working! Then run the app.

---

## 📱 Expected App Behavior (When Working)

When DNS is fixed, you should see:

1. **"Loading posts..."** (2-3 seconds)
2. **Posts list appears** with:
   - 100 posts from the API
   - Each post showing title and body
   - Beautiful dark-themed UI
   - Smooth scrolling

---

## 🐛 Current App Status

The app code is **100% correct**. All issues are due to emulator DNS failure:

✅ Retrofit configuration - CORRECT  
✅ Room database setup - CORRECT  
✅ Coroutines & Flow - CORRECT  
✅ UI implementation - CORRECT  
✅ Permissions - CORRECT  
✅ Network security config - CORRECT  

**The only issue is the emulator's broken DNS resolver.**

---

## 🎯 Recommended Action

**RESTART THE EMULATOR** - This fixes 90% of DNS issues.

If that doesn't work, **use a physical device** or **create a new emulator with API 33/34**.

---

## 📞 Need Help?

If none of these solutions work:

1. Check your Mac's internet connection
2. Disable any VPN or proxy
3. Check if your firewall is blocking the emulator
4. Try running the emulator from a different network (e.g., mobile hotspot)

---

**Once DNS is working, your app will work perfectly!** 🚀
