param([string]$Log = 'target-check.log', [string[]]$Goals = @('test'))
$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot'
$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if (-not $mavenCommand) {
    $mavenCommand = Get-Item -LiteralPath (Join-Path $env:USERPROFILE 'tools\apache-maven-3.10.0\bin\mvn.cmd')
}
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
& $mavenCommand @Goals --batch-mode --no-transfer-progress '-Ddebug=false' *> $Log
$result = $LASTEXITCODE
Get-Content -LiteralPath $Log -Tail 18
exit $result
