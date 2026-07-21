# Sistema de Mesa de Ayuda (Help Desk)
## SmartCity Solutions — Diseño de Patrones

Aplicación Java orientada a objetos que gestiona tickets de soporte aplicando **patrones GOF**, **principios SOLID** y **patrones GRASP**.

Cuenta con **doble interfaz** sobre la misma lógica de negocio:

- **Interfaz gráfica (Java Swing)** — se ejecuta por defecto. *(Reto adicional de la guía: valor agregado.)*
- **Interfaz de consola** — menú de texto, disponible con el argumento `consola`.

Ambas usan exactamente el mismo `HelpDeskFacade`, lo que demuestra el bajo acoplamiento: cambiar de consola a Swing no modificó ni una línea de la lógica.

---

## Cómo ejecutar en NetBeans

1. **Archivo → Abrir proyecto…**
2. Selecciona la carpeta `diseño_de_patrones`
3. Abre **HelpDesk_SmartCity**
4. Clic derecho → **Ejecutar** (o `F6`) → abre la **ventana Swing**

Guía detallada: `NETBEANS.md`

## Cómo ejecutar en terminal

```bash
cd "/Users/marionzuloagazapana/Downloads/diseño_de_patrones"

# Interfaz gráfica (Swing) — por defecto
./ejecutar.sh

# Interfaz de consola
./ejecutar.sh consola
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
├── vista/         → MenuPrincipal (consola) + VentanaLogin y VentanaPrincipal (Swing)
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
- Persistencia en memoria (`ArrayList`) y permanente en `data/helpdesk.json`

---

## Persistencia JSON — reto adicional

La aplicación utiliza **Gson** para guardar usuarios y tickets en
`data/helpdesk.json`. El archivo se crea automáticamente en la primera ejecución
y se actualiza después de crear, modificar, eliminar, asignar, avanzar o cancelar
un ticket.

Al cerrar y volver a abrir la aplicación se conservan:

- usuarios y credenciales de demostración;
- tickets y sus tipos;
- prioridad, estado y fecha de creación;
- solicitante y técnico asignado;
- el dato específico de INCIDENTE, CONSULTA o MEJORA.

Esto eleva la persistencia de nivel **básico (solo ArrayList)** a nivel
**intermedio (JSON)** según la guía del proyecto. Los `ArrayList` siguen siendo
las colecciones de trabajo y el JSON actúa como almacenamiento permanente.

---

## Interfaz gráfica (Java Swing) — reto adicional

La guía del proyecto contempla **Java Swing** como reto adicional que otorga reconocimiento extra. Se implementó sobre la misma arquitectura:

- **`VentanaLogin`** — inicio de sesión con usuarios de prueba a la vista.
- **`VentanaPrincipal`** — tabla de tickets y botones para todas las operaciones (registrar, actualizar, eliminar, asignar técnico, avanzar estado, cancelar, usuarios, historial).
- Los permisos del **Proxy** siguen activos: un cliente que intenta eliminar recibe *"Acceso denegado"*, igual que en consola.

Punto clave de diseño: la GUI **no contiene lógica de negocio**, solo llama a `HelpDeskFacade`. Por eso conviven la consola y Swing sin duplicar código (evidencia de **bajo acoplamiento** y patrón **Facade**).
