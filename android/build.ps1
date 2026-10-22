# Ricrea «Dejavu-Staff.apk» nella radice della repo (l'app per il tablet Android).
# Serve solo a chi modifica il codice: chi usa l'app installa direttamente l'APK.
#
# Uso (PowerShell, dalla radice della repo):   .\android\build.ps1
# La prima volta scarica Java, Android SDK e Gradle in .android-tools (fuori dalla repo, una cartella sopra):
# non installa niente nel sistema. La prima build richiede qualche minuto.
#
# La chiave di firma è in android\firma\ (NON va nella repo): conservane una copia!
# Senza la stessa chiave il tablet non accetta gli aggiornamenti e bisogna disinstallare l'app (perdendo i dati).
param([switch]$SoloStrumenti)
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$Android = $PSScriptRoot
$Radice = Split-Path -Parent $Android
$Strumenti = Join-Path (Split-Path -Parent $Radice) ".android-tools"
New-Item -ItemType Directory -Force $Strumenti | Out-Null

function Scarica($url, $nome, $cartella) {
    $dest = Join-Path $Strumenti $cartella
    $fatto = Join-Path $dest ".completo"
    if (Test-Path $fatto) { return $dest }
    $zip = Join-Path $Strumenti "$nome.zip"
    if (-not (Test-Path $zip)) {
        Write-Host "Scarico $nome ..."
        Invoke-WebRequest $url -OutFile "$zip.parziale" -UseBasicParsing
        Move-Item "$zip.parziale" $zip
    }
    # estrae direttamente nella cartella finale, senza la cartella principale dello zip
    # (niente spostamenti di cartelle: l'antivirus blocca i file appena estratti)
    New-Item -ItemType Directory -Force $dest | Out-Null
    & tar.exe -xf $zip -C $dest --strip-components=1
    if ($LASTEXITCODE -ne 0) { throw "Estrazione di $nome non riuscita" }
    New-Item -ItemType File $fatto | Out-Null
    Remove-Item $zip
    return $dest
}

# Java 17 (Eclipse Temurin), Gradle, Android SDK
$Jdk = Scarica "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk" "jdk17" "jdk-17"
$Gradle = Scarica "https://services.gradle.org/distributions/gradle-8.10.2-bin.zip" "gradle" "gradle-8.10.2"
$Sdk = Join-Path $Strumenti "android-sdk"
$Cmdline = Join-Path $Sdk "cmdline-tools\latest"
Scarica "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip" "cmdline-tools" "android-sdk\cmdline-tools\latest" | Out-Null
$env:JAVA_HOME = $Jdk
$env:ANDROID_HOME = $Sdk
$env:Path = "$Jdk\bin;$Gradle\bin;$env:Path"

$SdkPronto = { (Test-Path (Join-Path $Sdk "platforms\android-35\android.jar")) -and (Test-Path (Join-Path $Sdk "build-tools\35.0.0\source.properties")) }
if (-not (& $SdkPronto)) {
    Write-Host "Installo Android SDK 35 (accetta le licenze dell'Android SDK) ..."
    $sm = Join-Path $Cmdline "bin\sdkmanager.bat"
    $si = ("y`n" * 20)
    $si | & $sm --sdk_root=$Sdk --licenses | Out-Null
    # l'antivirus a volte blocca un file durante l'estrazione: si riprova
    for ($i = 0; $i -lt 3 -and -not (& $SdkPronto); $i++) {
        $si | & $sm --sdk_root=$Sdk "platforms;android-35" "build-tools;35.0.0" | Out-Null
    }
    if (-not (& $SdkPronto)) { throw "Installazione dell'Android SDK non riuscita" }
}
if ($SoloStrumenti) { Write-Host "Strumenti pronti in $Strumenti"; return }

# keytool e Gradle scrivono i loro messaggi su stderr: non vanno trattati come errori (si controlla $LASTEXITCODE)
$ErrorActionPreference = "Continue"

# Chiave di firma: creata una volta sola, poi sempre la stessa
$Firma = Join-Path $Android "firma"
$Jks = Join-Path $Firma "dejavu.jks"
$Props = Join-Path $Firma "firma.properties"
if (-not (Test-Path $Jks)) {
    New-Item -ItemType Directory -Force $Firma | Out-Null
    $pw = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
    & (Join-Path $Jdk "bin\keytool.exe") -genkeypair -keystore $Jks -alias dejavu -keyalg RSA -keysize 2048 -validity 36500 `
        -storepass $pw -keypass $pw -dname "CN=Deja Vu Staff, O=Deja Vu, L=Bergamo, C=IT" | Out-Null
    if ($LASTEXITCODE -ne 0) { throw "Creazione della chiave di firma non riuscita" }
    "storeFile=dejavu.jks`nstorePassword=$pw`nkeyAlias=dejavu`nkeyPassword=$pw`n" | Set-Content -Encoding ascii $Props
    Write-Host "Creata la chiave di firma in $Firma : CONSERVANE UNA COPIA."
}

"sdk.dir=$($Sdk -replace '\\', '/')" | Set-Content -Encoding ascii (Join-Path $Android "local.properties")
Push-Location $Android
try {
    & gradle --no-daemon -q assembleRelease
    if ($LASTEXITCODE -ne 0) { throw "Build non riuscita" }
} finally { Pop-Location }

$Apk = Join-Path $Android "app\build\outputs\apk\release\app-release.apk"
Copy-Item $Apk (Join-Path $Radice "Dejavu-Staff.apk") -Force
Write-Host ""
Write-Host "Fatto: $(Join-Path $Radice 'Dejavu-Staff.apk')"
