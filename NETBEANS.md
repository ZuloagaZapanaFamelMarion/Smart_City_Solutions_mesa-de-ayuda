# Cómo abrir y ejecutar en NetBeans

## Pasos

1. Abre **Apache NetBeans**.
2. Menú **Archivo → Abrir proyecto…** (File → Open Project).
3. Selecciona la carpeta:
   `Documents/diseño_de_patrones`
4. Debe aparecer el proyecto **HelpDesk_SmartCity**.
5. Pulsa **Abrir proyecto**.
6. Clic derecho en el proyecto → **Ejecutar** (Run).
   También: botón verde ▶ o `F6`.

## Clase principal

`main.Main`

Si te pide elegir Main Class:
- Clic derecho del proyecto → **Propiedades** → **Ejecutar**
- Main Class: `main.Main`

## Usuarios de prueba

| Email | Password | Rol |
|-------|----------|-----|
| maria@ciudad.com | cli123 | CLIENTE |
| ana@helpdesk.com | tec123 | TÉCNICO |
| admin@helpdesk.com | admin123 | ADMIN |

## Si NetBeans no lo reconoce

1. Confirma que existen `build.xml` y la carpeta `nbproject/`.
2. Usa **Archivo → Abrir proyecto** (no “Abrir archivo”).
3. Alternativa: **Archivo → Proyecto nuevo → Java con Ant → Java Free-Form Project**
   y apunta a esta misma carpeta.

## Compilar por terminal (opcional)

```bash
cd "/Users/marionzuloagazapana/Documents/diseño_de_patrones"
ant run
```
