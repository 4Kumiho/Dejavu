# Prova «Dejavu-Staff.apk» su un tablet Android virtuale (emulatore), senza un tablet vero.
#
# Uso (PowerShell, dalla radice della repo):   .\android\prova-emulatore.ps1
# La prima volta scarica l'emulatore e Android 15 (circa 2 GB) in .android-tools e crea il tablet «TabletBar».
# Poi apre la finestra del tablet, installa l'APK della repo e avvia l'app.
# Prima crea l'APK con .\android\build.ps1 (che prepara anche gli strumenti).
$ErrorActionPreference = "Stop"
$Radice = Split-Path -Parent $PSScriptRoot
$Strumenti = Join-Path (Split-Path -Parent $Radice) ".android-tools"
$Sdk = Join-Path $Strumenti "android-sdk"
$env:JAVA_HOME = Join-Path $Strumenti "jdk-17"
$env:ANDROID_SDK_ROOT = $Sdk
$env:ANDROID_AVD_HOME = Join-Path $Strumenti "avd"
$Bin = Join-Path $Sdk "cmdline-tools\latest\bin"
$Adb = Join-Path $Sdk "platform-tools\adb.exe"
$Emulatore = Join-Path $Sdk "emulator\emulator.exe"
$Apk = Join-Path $Radice "Dejavu-Staff.apk"
$Immagine = "system-images;android-35;google_apis;x86_64"
if (-not (Test-Path $Bin)) { throw "Strumenti mancanti: lancia prima .\android\build.ps1" }
if (-not (Test-Path $Apk)) { throw "Manca Dejavu-Staff.apk: lancia prima .\android\build.ps1" }

# l'antivirus a volte blocca un file durante l'estrazione: si riprova
$si = ("y`n" * 20)
$pronto = { (Test-Path $Emulatore) -and (Test-Path $Adb) -and (Test-Path (Join-Path $Sdk "system-images\android-35\google_apis\x86_64\system.img")) }
for ($i = 0; $i -lt 3 -and -not (& $pronto); $i++) {
    Write-Host "Scarico emulatore e Android 15 (la prima volta, circa 2 GB) ..."
    $si | & (Join-Path $Bin "sdkmanager.bat") --sdk_root=$Sdk "emulator" "platform-tools" $Immagine | Out-Null
}
if (-not (& $pronto)) { throw "Installazione dell'emulatore non riuscita" }

$ErrorActionPreference = "Continue" # adb e avdmanager scrivono messaggi su stderr
if (-not (Test-Path (Join-Path $env:ANDROID_AVD_HOME "TabletBar.avd"))) {
    Write-Host "Creo il tablet virtuale TabletBar ..."
    New-Item -ItemType Directory -Force $env:ANDROID_AVD_HOME | Out-Null
    "no" | & (Join-Path $Bin "avdmanager.bat") create avd -n TabletBar -k $Immagine -d pixel_tablet --force | Out-Null
}

if (-not ((& $Adb devices) -match "emulator-\d+\s+device")) {
    Write-Host "Avvio il tablet virtuale (la prima volta ci mette un paio di minuti) ..."
    Start-Process $Emulatore -ArgumentList "-avd", "TabletBar", "-no-boot-anim", "-gpu", "swiftshader_indirect"
    & $Adb wait-for-device
    while ((& $Adb shell getprop sys.boot_completed 2>$null) -notmatch "1") { Start-Sleep 3 }
}

Write-Host "Installo l'APK ..."
& $Adb install -r $Apk
& $Adb shell am start -n it.dejavu.staff/.MainActivity | Out-Null
Write-Host ""
Write-Host "Fatto: l'app è aperta nella finestra del tablet virtuale."
Write-Host "Per riprovare dopo una nuova build basta rilanciare questo script (il tablet resta acceso)."
