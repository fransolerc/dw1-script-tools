# DW1 Script Parser

A tooling project for parsing and visualizing the script dump from **Digimon World 1** (PlayStation 1, 1999).

## What it does

Takes the raw script dump (`Script US.txt`) and transforms it into structured, manageable JSON files. It also generates an interactive HTML dashboard to explore the game's logic and dialogues.

**Pipeline:**
```
Script US.txt → ScriptParser → ScriptModularizer → 222 JSON files + manifest.json
                                                  → scripts_dashboard.html
```

## Project structure

```
DW1-demo/
├── raw_data/
│   └── Script US.txt          # Raw script dump (opcodes, dialogues, events)
├── scripts/                   # Generated output (one JSON per script + manifest)
├── src/main/kotlin/com/dw1demo/
│   ├── model/
│   │   ├── ScriptModels.kt    # ScriptData, ScriptBlock, SectionBlock, Instruction
│   │   └── ManifestModels.kt  # ScriptManifest, ManifestEntry
│   ├── parser/
│   │   ├── ScriptParser.kt    # Parses Script US.txt into ScriptData
│   │   └── ScriptMigrator.kt  # Saves full ScriptData to a single JSON file
│   ├── modularizer/
│   │   └── ScriptModularizer.kt  # Splits ScriptData into individual JSON files
│   ├── visualizer/
│   │   └── ScriptVisualizer.kt   # Generates the HTML dashboard
│   ├── util/
│   │   └── FindAddress.kt     # Utility to locate a specific address across all scripts
│   └── Main.kt                # Entry point
├── items.json                 # Item ID → name mapping
├── triggers.json              # Trigger ID → name mapping
└── scripts_dashboard.html     # Generated dashboard (open with a local server)
```

## How to run

### Requirements
- JDK 21
- Gradle

### Steps

1. Place `Script US.txt` inside the `raw_data/` folder.
2. Run the application:
   ```bash
   ./gradlew run
   ```
3. The tool will generate the `scripts/` folder and `scripts_dashboard.html`.

> If `scripts/manifest.json` already exists, the tool will skip generation. Delete the `scripts/` folder to force a full regeneration.

### Viewing the dashboard

The dashboard uses `fetch()` to load scripts on demand, so it requires a local server due to browser CORS restrictions. The simplest way:

```bash
python -m http.server 8080
```

Then open `http://localhost:8080/scripts_dashboard.html` in your browser.

## Script format

The raw dump contains scripts with the following structure:

```
== Script ID 115 == 52800
Section_254:
000016 storeRandom 110 99
000020 if pstat(110) > 18 then 38
000032 spawnItem 44 56 34
000200 showTextbox Welcome young man.\n
000242 playAnimation 253 20
```

Each script has one or more sections, and each section contains a sequence of instructions with a memory address, an opcode, and its arguments.

## Output format

Each script is saved as an individual JSON file:

```json
{
  "id": 115,
  "info": "== Script ID 115 == 52800",
  "sections": {
    "254": {
      "id": 254,
      "instructions": [
        {
          "address": 16,
          "opcode": "storeRandom",
          "args": ["110", "99"],
          "lineNumber": 3
        },
        {
          "address": 200,
          "opcode": "showTextbox",
          "args": ["Welcome young man.\\n"],
          "lineNumber": 10
        }
      ]
    }
  }
}
```

## Notes

- `items.json` and `triggers.json` are used by the dashboard to translate numeric IDs into readable names.
- This project is tooling only. The game engine (LibGDX) lives in a separate repository.
