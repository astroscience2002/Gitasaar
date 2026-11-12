# Gita Saar — Play Store Launch Instructions

## Current build

- Android 16 / API 36 target, min Android 8 / API 26.
- Native Android application with 18 chapters and **700 bundled verses**.
- The release candidate includes original Sanskrit, Hindi and English meanings.
- The 700-verse variant omits the optional extra Arjuna question at the start of chapter 13 and renumbers that chapter. The original source and credits are in THIRD_PARTY_LICENSES.md.
- Krishna artwork is packaged during builds from 3 public-domain works archived on Wikimedia Commons. Source and credit: THIRD_PARTY_LICENSES.md.
- English and Hindi app labels, local bookmarks, quotes, relevant verse guidance, next/previous verse, adjustable type size and Android text-to-speech (device-dependent).
- User-requested connection check still requires Internet to open the reader.

## Quick test APK

Open GitHub Actions > **Build Android Test APK** > newest successful run > Artifacts > GitaSaar-debug-APK. Extract the artifact ZIP and install its APK on an Android device. It has **Google AdMob TEST** ad IDs. It is not a Play upload artifact.

## Signed Play Store AAB (safe upload-key handling)

The default Android build creates an **UNSIGNED** AAB. It cannot be directly uploaded to production. Do not commit any keystore or password to GitHub.

1. Create your own long-lived upload key with Android Studio or `keytool` and back it up securely. Do not share the password or keystore in public issues, code, or chats.
2. In your repository, set GitHub Actions **Secrets**:
   - `PLAY_UPLOAD_KEYSTORE_B64`: base64 encoding of the entire private .jks file
   - `PLAY_UPLOAD_STORE_PASSWORD`
   - `PLAY_UPLOAD_KEY_ALIAS`
   - `PLAY_UPLOAD_KEY_PASSWORD`
   - `ADMOB_APP_ID` (optional during testing; required for monetization)
   - `ADMOB_BANNER_ID` (optional during testing; required for monetization)
3. In Actions, manually run **Build Signed Play Store AAB**. It restores the keystore in the build machine, signs the app and uploads the resulting AAB as an artifact. Do not commit your keystore.
4. Use Play App Signing and upload the signed release AAB in Google Play Console.

## Google AdMob

Create an AdMob account and register your app. Configure the actual App ID and Banner Ad Unit ID in GitHub secrets. The public release build **does not serve ads** until BOTH real IDs are supplied. The debug build uses test IDs only. Make sure your privacy policy, Consent Management Platform / UMP consent messaging, and Play Data safety disclosures reflect the chosen ads configuration.

## Before production

- Confirm that the package ID `com.gitasaar.reader` is available and belongs to you **before first public release**. Changing it after publishing creates a different app.
- Add a real developer email and published privacy policy URL, completed Data safety and content rating; confirm whether your app contains ads.
- Capture real Play Store screenshots from the installed app; do not submit AI-generated UI mockups as screenshots.
- Test the 700 verses and translations against a trusted edition, especially chapter 13 numbering. Validate daily notifications, languages, search, guidance, app restart, online-only gate, back navigation, and accessibility on several real phones.
- The text-to-speech engine varies by phone and Hindi voice installation. Do not advertise guaranteed studio-quality verse audio.
- Verify Android application quality, artwork licenses in your jurisdiction, and AdMob policy.
- New personal developer accounts created after November 13, 2023 may require 12 opted-in testers for 14 continuous days before requesting production access.
- App must be reviewed and approved by Google Play. Test build success is **not** Google Play approval.

## References

- https://support.google.com/googleplay/android-developer/answer/11926878
- https://support.google.com/googleplay/android-developer/answer/14151465
- https://support.google.com/googleplay/android-developer/answer/9859152
- https://developers.google.com/admob/android/quick-start
