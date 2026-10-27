# Survival Flight & Creative Inventory

Fly and use the creative inventory while staying in Survival mode.

Minecraft **1.21.1**, NeoForge **21.1.250 or later in the 21.1 branch**, Java **21**.

## Features

- Flight: double-tap Space, then use Space / Shift to move up / down.
- Creative inventory with tabs, search, and free items: press E.
- Survival health, hunger, mob damage, mining, item consumption, and durability remain active.
- Enabled state is stored with the player.
- Available only to the owner of a singleplayer world. Dedicated servers are not supported.

## Commands

Cheats do not need to be enabled.

- `/survivalplus on` — enable.
- `/survivalplus off` — disable.
- `/survivalplus` — enable both features, or disable both if both are already enabled.
- `/survivalplus flight` — toggle flight without changing the inventory setting.
- `/survivalplus inventory` — toggle creative inventory without changing the flight setting.
- `/survivalplus flight on` / `/survivalplus flight off` — explicitly enable / disable flight.
- `/survivalplus inventory on` / `/survivalplus inventory off` — explicitly enable / disable creative inventory.

`/survivalplus on` and `/survivalplus off` enable / disable both features.
Independent settings persist after death and rejoining. Existing 1.0.1 settings are preserved on upgrade.

Land before disabling flight: fall damage applies again after disabling.
The internal mod ID and commands remain `survivalplus`.

## Download

[Download Survival Flight & Creative Inventory 1.1.0 for Minecraft 1.21.1 / NeoForge 21.1.250](downloads/survivalplus-1.1.0-neoforge-21.1.250.jar)

Replace the old mod JAR in your NeoForge profile’s `mods` folder with this file. Do not keep both versions installed.

## Build and install

With JDK 21 installed, run `./gradlew build` (Windows: `gradlew.bat build`).
The first build downloads dependencies. Find the mod JAR in `build/libs/`
and place it in the `mods` folder of your Minecraft 1.21.1 NeoForge profile.
Do not install a sources JAR as the mod.

## Verification status

Source code has been reviewed and `./gradlew build --offline --no-daemon`
completed successfully with JDK 21. All 36 command-state combinations passed.
Automated gameplay tests are not included.
Flight and inventory behavior have not been verified in a running game.

## Русский

Мод добавляет полёт и меню креатива с бесплатными предметами в выживании.
Здоровье, голод и обычные механики выживания сохраняются.
Работает только у владельца одиночного мира. Управление: команды выше,
`/survivalplus flight` — отдельно полёт, `/survivalplus inventory` — отдельно инвентарь.
E — инвентарь, двойной пробел — полёт, пробел / Shift — вверх / вниз.
Перед отключением приземлитесь.
