$ErrorActionPreference = 'Stop'

# Detecta JAVA_HOME en Windows buscando JDK instalado.
$jdkCandidates = Get-ChildItem 'C:\Program Files\Java' -Directory -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -like 'jdk*' } |
    Sort-Object Name -Descending

if (-not $jdkCandidates -or $jdkCandidates.Count -eq 0) {
    throw 'No se encontró un JDK en C:\Program Files\Java. Instala JDK 17+ y vuelve a intentar.'
}

$detectedJavaHome = $jdkCandidates[0].FullName

$env:JAVA_HOME = $detectedJavaHome
Write-Host "JAVA_HOME configurado en esta sesión: $env:JAVA_HOME"

Set-Location $PSScriptRoot

# Arranca Spring Boot sin tests para iterar más rápido en desarrollo
.\mvnw.cmd -DskipTests spring-boot:run
