# seed.ps1 - Genera datos de prueba para CampusHub via API
# Uso (con la app arrancada):  powershell -ExecutionPolicy Bypass -File .\seed.ps1
# Opcional: .\seed.ps1 -Base http://localhost:8080
param([string]$Base = "http://localhost:8080")

$ErrorActionPreference = "Stop"
$run = Get-Date -Format "HHmmss"   # sufijo para que los emails no se repitan entre ejecuciones

function Call {
    param([string]$Method, [string]$Path, $Body = $null)
    $uri = "$Base$Path"
    try {
        if ($null -ne $Body) {
            $json  = $Body | ConvertTo-Json -Depth 8 -Compress
            $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)
            $r = Invoke-WebRequest -Uri $uri -Method $Method -ContentType "application/json; charset=utf-8" -Body $bytes -UseBasicParsing
        } else {
            $r = Invoke-WebRequest -Uri $uri -Method $Method -UseBasicParsing
        }
        $obj = $null
        if ($r.Content) { $obj = $r.Content | ConvertFrom-Json }
        return @{ status = [int]$r.StatusCode; body = $obj }
    } catch {
        $code = 0
        if ($_.Exception.Response) { $code = [int]$_.Exception.Response.StatusCode }
        return @{ status = $code; body = $null; error = $_.Exception.Message }
    }
}

function Create {
    param([string]$What, [string]$Path, $Body)
    $r = Call "POST" $Path $Body
    if ($r.status -ne 201 -and $r.status -ne 200) {
        Write-Host ("  [ERROR] {0} -> HTTP {1} {2}" -f $What, $r.status, $r.error) -ForegroundColor Red
        throw "Fallo creando $What. Para aqui."
    }
    Write-Host ("  [OK] {0} (id {1})" -f $What, $r.body.id)
    return $r.body.id
}

Write-Host "== CampusHub seed ($Base) ==" -ForegroundColor Cyan

# ---- Departamentos ----
Write-Host "Departamentos"
$deps = @()
foreach ($n in @("Informatica", "Matematicas", "Idiomas")) {
    $deps += Create "Departamento $n" "/api/departamentos" @{ nombre = "$n $run" }
}

# ---- Docentes ----
Write-Host "Docentes"
$docNombres = @(
    @("Laura", "Martinez Ruiz"), @("Carlos", "Gomez Lopez"),
    @("Elena", "Navarro Sanz"),  @("Pablo", "Diaz Herrera")
)
$docs = @()
for ($i = 0; $i -lt $docNombres.Count; $i++) {
    $docs += Create ("Docente " + $docNombres[$i][0]) "/api/docentes" @{
        nombre = $docNombres[$i][0]; apellidos = $docNombres[$i][1]
        email = ("docente{0}.{1}@campus.test" -f $i, $run)
        departamentoId = $deps[$i % $deps.Count]
    }
}

# ---- Grupos ----
Write-Host "Grupos"
$grupos = @()
$grupoNombres = @("1DAM", "2DAM", "2DAW")
for ($i = 0; $i -lt $grupoNombres.Count; $i++) {
    $grupos += Create ("Grupo " + $grupoNombres[$i]) "/api/grupos" @{
        nombre = ("{0}-{1}" -f $grupoNombres[$i], $run); nivel = "FP Superior"
        tutorDocenteId = $docs[$i]
    }
}

# ---- Estudiantes (12) ----
Write-Host "Estudiantes"
$nombres = @("Ana","Luis","Marta","Jorge","Lucia","Sergio","Paula","Hugo","Irene","Dario","Noa","Marcos")
$apellidos = @("Perez Vega","Soto Mena","Ruiz Calvo","Lara Gil","Mora Pons","Rey Ortiz","Cano Ibanez","Pardo Luna","Vidal Roca","Crespo Haro","Mendez Ros","Bravo Salas")
$ests = @()
for ($i = 0; $i -lt $nombres.Count; $i++) {
    $ests += Create ("Estudiante " + $nombres[$i]) "/api/estudiantes" @{
        nombre = $nombres[$i]; apellidos = $apellidos[$i]
        email = ("{0}.{1}@alumnos.test" -f $nombres[$i].ToLower(), $run)
        fechaNacimiento = ("200{0}-0{1}-1{2}" -f (3 + ($i % 5)), (1 + ($i % 9)), ($i % 9))
        grupoId = $grupos[$i % $grupos.Count]
    }
}

# ---- Actividades ----
Write-Host "Actividades"
$actDefs = @(
    @{ t = "Taller de MongoDB";    tipo = "TALLER";  max = 5;  d = 0; f = "2027-03-10" },
    @{ t = "Charla de ciberseguridad"; tipo = "CHARLA"; max = 30; d = 1; f = "2027-03-18" },
    @{ t = "Visita a empresa";     tipo = "VISITA";  max = 3;  d = 2; f = "2027-04-02" },
    @{ t = "Jornada de puertas abiertas"; tipo = "JORNADA"; max = 50; d = 3; f = "2027-05-06" }
)
$acts = @()
foreach ($a in $actDefs) {
    $acts += Create ("Actividad " + $a.t) "/api/actividades" @{
        titulo = $a.t; descripcion = ("Descripcion de " + $a.t); tipo = $a.tipo
        fechaPrevista = $a.f; maxParticipantes = $a.max; docenteId = $docs[$a.d]
    }
}

# ---- Inscripciones ----
Write-Host "Inscripciones"
function Inscribir($actId, $estId) {
    $r = Call "POST" "/api/actividades/$actId/inscripciones" @{ estudianteId = $estId }
    return $r.status
}
# Actividad 0 (max 5): 4 alumnos  -> quedan 1 plaza
0..3 | ForEach-Object { [void](Inscribir $acts[0] $ests[$_]) }
Write-Host "  Taller de MongoDB: 4 inscritos de 5"
# Actividad 1 (max 30): 8 alumnos
0..7 | ForEach-Object { [void](Inscribir $acts[1] $ests[$_]) }
Write-Host "  Charla: 8 inscritos de 30"
# Actividad 2 (max 3): se llena con 3 y el 4o debe dar 409
0..2 | ForEach-Object { [void](Inscribir $acts[2] $ests[$_]) }
$st = Inscribir $acts[2] $ests[3]
if ($st -eq 409) { Write-Host "  Visita: llena (3/3) y el 4o alumno recibe 409 como se esperaba" -ForegroundColor Green }
else { Write-Host "  Visita: el 4o alumno devolvio $st (se esperaba 409)" -ForegroundColor Yellow }
# El alumno 0 tambien en la jornada (para ver historial con varias)
[void](Inscribir $acts[3] $ests[0])

# ---- Seguimientos (MongoDB) ----
Write-Host "Seguimientos (MongoDB)"
function Seg($actId, $tipo, $datos) {
    $r = Call "POST" "/api/actividades/$actId/seguimientos" @{ tipo = $tipo; datos = $datos }
    if ($r.status -eq 201 -or $r.status -eq 200) { Write-Host ("  [OK] {0} en actividad {1} (id {2})" -f $tipo, $actId, $r.body.id) }
    else { Write-Host ("  [ERROR] {0} en actividad {1} -> HTTP {2}" -f $tipo, $actId, $r.status) -ForegroundColor Red }
}
Seg $acts[0] "VISITA"     @{ empresa = "Acme"; observaciones = @("aprendizaje", "visita"); asistentes = 4 }
Seg $acts[0] "VALORACION" @{ nota = 8.5; comentario = "Muy util"; recomendaria = $true }
Seg $acts[0] "ASISTENCIA" @{ presentes = @("Ana", "Luis", "Marta"); ausentes = @("Jorge") }
Seg $acts[1] "NOTA"       @{ texto = "Buena participacion"; etiquetas = @("seguridad", "charla") }
Seg $acts[2] "INCIDENCIA" @{ gravedad = "baja"; detalle = "Autobus con 10 min de retraso" }

# ---- Resumen ----
Write-Host ""
Write-Host "== RESUMEN ==" -ForegroundColor Cyan
Write-Host ("Departamentos: {0}  Docentes: {1}  Grupos: {2}  Estudiantes: {3}  Actividades: {4}" -f $deps.Count, $docs.Count, $grupos.Count, $ests.Count, $acts.Count)
Write-Host ""
Write-Host "Prueba estas URLs en el navegador:"
for ($i = 0; $i -lt $acts.Count; $i++) {
    Write-Host ("  Detalle actividad {0}: {1}/api/actividades/{0}/detalle" -f $acts[$i], $Base)
}
Write-Host ("  Historial estudiante {0}: {1}/api/estudiantes/{0}/actividades" -f $ests[0], $Base)
Write-Host ""
Write-Host "Esperado: actividad $($acts[0]) -> 4 ocupadas / 1 disponible / 3 seguimientos;"
Write-Host "          actividad $($acts[2]) -> 3 ocupadas / 0 disponibles / 1 seguimiento;"
Write-Host "          estudiante $($ests[0]) -> 4 actividades en el historial."
