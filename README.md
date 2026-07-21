# Sistema de Mesa de Ayuda (Help Desk)
## SmartCity Solutions — Diseño de Patrones

Aplicación Java orientada a objetos que gestiona tickets de soporte aplicando **patrones GOF**, **principios SOLID** y **patrones GRASP**.

---

## Cómo ejecutar en NetBeans

1. **Archivo → Abrir proyecto…**
2. Selecciona la carpeta `diseño_de_patrones`
3. Abre **HelpDesk_SmartCity**
4. Clic derecho → **Ejecutar** (o `F6`)

Guía detallada: `NETBEANS.md`

## Cómo ejecutar en terminal

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
cd "/Users/marionzuloagazapana/Documents/diseño_de_patrones"
./ejecutar.sh
# o: ant run
```

### Usuarios de prueba

| Email | Password | Rol |
|-------|----------|-----|
| admin@helpdesk.com | admin123 | ADMINISTRADOR |
| ana@helpdesk.com | tec123 | TÉCNICO |
| luis@helpdesk.com | tec123 | TÉCNICO |
| maria@ciudad.com | cli123 | CLIENTE |
| pedro@ciudad.com | cli123 | CLIENTE |

---

## Arquitectura de paquetes

```
src/
├── modelo/        → Entidades (Usuario, Ticket y subtipos)
├── vista/         → Menú consola
├── controlador/   → TicketService (lógica de negocio)
├── interfaces/    → Abstracciones (DIP / ISP)
├── singleton/     → SistemaConfig, SesionUsuario
├── factory/       → TicketFactory
├── facade/        → HelpDeskFacade
├── proxy/         → TicketServiceProxy
├── observer/      → HistorialNotificaciones
├── state/         → Estados del ticket
├── util/          → Validador, RepositorioDatos (ArrayList)
└── main/          → Main
```

---

## Patrones GOF implementados (6+)

| Patrón | Tipo | Dónde | Problema que resuelve |
|--------|------|-------|------------------------|
| **Singleton** | Creacional | `SistemaConfig`, `SesionUsuario` | Una sola configuración / sesión global |
| **Factory** | Creacional | `TicketFactory` | Crear INCIDENTE, CONSULTA o MEJORA sin acoplar la vista |
| **Facade** | Estructural | `HelpDeskFacade` | Simplificar login, CRUD y flujos para el menú |
| **Proxy** | Estructural | `TicketServiceProxy` | Controlar permisos al eliminar / cambiar estado |
| **Observer** | Comportamiento | `Ticket` + `HistorialNotificaciones` + usuarios | Notificar cambios de estado |
| **State** | Comportamiento | `EstadoNuevo` → `EnProceso` → `Resuelto` → `Cerrado` | Ciclo de vida del ticket |

---

## SOLID

- **SRP**: cada clase una responsabilidad (Validador, Repositorio, Service, Vista…)
- **OCP**: nuevos tipos de ticket vía Factory; nuevos estados vía `IEstadoTicket`
- **LSP**: `Cliente` / `Tecnico` / `Administrador` sustituyen a `Usuario`; subtickets a `Ticket`
- **ISP**: interfaces pequeñas (`IObservador`, `INotificable`, `IEstadoTicket`)
- **DIP**: `TicketServiceProxy` y Facade dependen de `ITicketService`

## GRASP

Alta cohesión, bajo acoplamiento, Controlador (`TicketService` / Facade), Experto (`Ticket`, `Usuario`), Creador, Polimorfismo, Fabricación pura (`TicketFactory`, `Validador`).

---

## Funcionalidades

- Login con roles
- CRUD de tickets
- Asignación de técnico
- Avance / cancelación de estados
- Validaciones
- Notificaciones e historial
- Persistencia en memoria (`ArrayList`)
