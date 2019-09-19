## [Hodl GRS Wallet](https://hodlwallet.com/) - The easy and secure groestlcoin wallet for Android

[![download](/images/icon-google-play.png)](https://play.google.com/store/apps/details?id=org.groestlcoin.hodlwallet)

### Groestlcoin done right

This is the Android port of the Hodl GRS Wallet iOS app, which can be found [here](https://github.com/hodlwallet/hodl-wallet-ios).

### A completely standalone Groestlcoin wallet

Unlike other Andriod Groestlcoin wallets, **Hodl** is a standalone Groestlcoin client. It connects directly to the Groestlcoin network using [SPV](https://en.bitcoin.it/wiki/Thin_Client_Security#Header-Only_Clients) mode, and doesn't rely on servers that can be hacked or disabled. Even if **Hodl** disappears, the app will continue to function, allowing users to access their money at any time. You can also recover **Hodl's** backup up recovery key in other Groestlcoin wallets if necessary as we follow BIP standards for wallet generation.

### The next step in wallet security

**Hodl** utilizes AES hardware encryption, app sandboxing, and the latest security features to protect users from malware, browser security holes, and even physical theft. Private keys are stored only in the secure enclave of the user's phone, inaccessible to anyone other than the user.

### Simple yet robust

Simplicity and ease-of-use is **Hodl**'s core design principle. A simple recovery phrase (which we call a Backup Recovery Key) is all that is needed to restore the user's wallet if they ever lose or replace their device. **Hodl** is [deterministic](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki), which means the user's balance and transaction history can be recovered just from the backup recovery key.

### Features

- [Simplified payment verification](https://github.com/bitcoin/bips/blob/master/bip-0037.mediawiki) for fast mobile performance
- No server to get hacked or go down
- Single backup phrase that works forever
- Private keys never leave your device
- Import [password protected](https://github.com/bitcoin/bips/blob/master/bip-0038.mediawiki) paper wallets
- [Payment protocol](https://github.com/bitcoin/bips/blob/master/bip-0070.mediawiki) payee identity certification

### Localization

**Hodl GRS** is available in the following languages:

- Chinese (Simplified and traditional)
- Danish
- Dutch
- English
- French
- German
- Italian
- Japanese
- Korean
- Portuguese
- Russian
- Spanish
- Swedish


### Recovering your funds with other Groestlcoin Wallets.

It may be necessary at somepoint to recover your Groestlcoin via your Hodl Wallet Backup Recovery Key (seed) using another Groestlcoin wallet. In this case it is important to know what path Hodl Wallet derives your keys from. 

### Derivation path

Hodl GRS Wallet conforms to [BIP32](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki) standards when generating your wallet. 

Derivation Path - `m/0'`

### Recovering funds with Electrum

If you find yourself with the need to recover your funds with an external app, Hodl GRS Wallet recommends [Groestlcoin Electrum](https://www.groestlcoin.org/groestlcoin-electrum-wallet/)

It's important that you make sure you our downloading Electrum from the real source. You can do this by double checking the ssl certificate at the site or building from source.

Steps to recovering your Groestlcoin using Electrum:

1. Select New/Restore
2. Name the Wallet
3. Select Standard Wallet
4. Select "I have the seed"
5. In options select BIP39
6. Enter the seed
7. Select Path `m/0'`

**NOTE:** If you wish to recover funds in legacy addresses you must repeat the process in Groestlcoin Electrum while the existing wallet showing your balance in the segwit addresses is open. Do this by selecting File --> New/Restore. Electrum allows a user to manage multiple wallets and seeds at once. 


### Contact

Should you need to get in contact with us for any reason please feel free to write us at support@groestlcoin.org

You can visit our site here: [Groestlcoin.org](https://www.groestlcoin.org) 


## How to set up the development environment:
1. Download and install Java 7 or up
2. Download and Install the latest Android studio
3. Download and install the latest NDK https://developer.android.com/ndk/downloads/index.html or download it in android studio by "choosing the NDK" and press "download"
4. Go to https://github.co/hodlwallet/breadwallet-android and clone or download the project
5. Open the project with Android Studio and let the project sync
6. Go to SDK Manager and download all the SDK Platforms and SDK Tools
7. Initialize the submodules - <code>git submodule init</code>
8. Update the submodules - <code>git submodule update</code>
9. Build -> Rebuild Project
