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
- `/survivalplus` — toggle.

Land before disabling flight: fall damage applies again after disabling.
The internal mod ID and commands remain `survivalplus`.

## Build and install

With JDK 21 installed, run `./gradlew build` (Windows: `gradlew.bat build`).
The first build downloads dependencies. Find the mod JAR in `build/libs/`
and place it in the `mods` folder of your Minecraft 1.21.1 NeoForge profile.
Do not install a sources JAR as the mod.

## Verification status

Source code has been reviewed and `./gradlew build --offline --no-daemon`
completed successfully with JDK 21. Automated gameplay tests are not included.
Flight and inventory behavior have not been verified in a running game.

## Русский

Мод добавляет полёт и меню креатива с бесплатными предметами в выживании.
Здоровье, голод и обычные механики выживания сохраняются.
Работает только у владельца одиночного мира. Управление: команды выше,
E — инвентарь, двойной пробел — полёт, пробел / Shift — вверх / вниз.
Перед отключением приземлитесь.
