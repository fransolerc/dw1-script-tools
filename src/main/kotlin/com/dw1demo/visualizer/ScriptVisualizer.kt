package com.dw1demo.visualizer

import com.dw1demo.model.ScriptManifest
import java.io.File

class ScriptVisualizer {

    fun generateDashboard(manifest: ScriptManifest, scriptsDir: File, outputFile: File) {
        val sortedEntries = manifest.entries.values.sortedBy { it.id }
        val sidebarItems = sortedEntries.joinToString("\n") { entry ->
            val preview = escapeHtml(entry.dialoguePreviews.firstOrNull() ?: "(Logic Only)")
            """<div class="script-item" onclick="loadScript(${entry.id}, '${entry.fileName}')" id="script-item-${entry.id}">
                <span class="id">Script ${entry.id}</span>
                <span class="preview">$preview</span>
            </div>"""
        }

        val itemsJson = File("items.json").let { if (it.exists()) it.readText() else "{}" }
        val triggersJson = File("triggers.json").let { if (it.exists()) it.readText() else "{}" }

        val html = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Digimon World Script Dashboard</title>
    <style>
        :root {
            --bg-color: #0d1117;
            --card-bg: #161b22;
            --border-color: #30363d;
            --text-primary: #c9d1d9;
            --text-secondary: #8b949e;
            --accent: #58a6ff;
            --highlight: #238636;
            --dialogue-bg: #1f242c;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
            background-color: var(--bg-color);
            color: var(--text-primary);
            margin: 0;
            display: flex;
            height: 100vh;
            overflow: hidden;
        }
        #sidebar {
            width: 300px;
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            background: var(--card-bg);
        }
        #search-container {
            padding: 16px;
            border-bottom: 1px solid var(--border-color);
        }
        #search-input {
            width: 100%;
            padding: 8px;
            background: var(--bg-color);
            border: 1px solid var(--border-color);
            color: white;
            border-radius: 6px;
            box-sizing: border-box;
        }
        #script-list { flex: 1; overflow-y: auto; padding: 10px; }
        .script-item {
            padding: 10px;
            border-radius: 6px;
            cursor: pointer;
            margin-bottom: 4px;
            border: 1px solid transparent;
        }
        .script-item:hover { background: rgba(255,255,255,0.05); }
        .script-item.active { background: var(--accent); color: white; }
        .script-item .id { font-weight: bold; margin-right: 8px; }
        .script-item .preview { font-size: 0.8em; color: var(--text-secondary); display: block; margin-top: 4px; }
        .script-item.active .preview { color: #eee; }
        #main-content { flex: 1; overflow-y: auto; padding: 40px; }
        .section {
            margin-bottom: 40px;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            background: var(--card-bg);
            overflow: hidden;
        }
        .section-header {
            padding: 12px 20px;
            background: rgba(255,255,255,0.05);
            border-bottom: 1px solid var(--border-color);
            font-weight: bold;
        }
        .instruction {
            padding: 8px 10px;
            border-bottom: 1px solid rgba(255,255,255,0.02);
            display: grid;
            grid-template-columns: 60px 150px 1fr;
            gap: 15px;
            align-items: start;
            font-family: monospace;
            font-size: 0.9em;
        }
        .instruction:last-child { border-bottom: none; }
        .instr-addr { color: var(--text-secondary); }
        .instr-op { color: var(--accent); font-weight: bold; }
        .dialogue-box {
            background: var(--dialogue-bg);
            padding: 12px;
            border-radius: 6px;
            border-left: 4px solid var(--accent);
            white-space: pre-wrap;
            color: white;
            font-family: sans-serif;
        }
        .tag-yellow { color: #f2cc60; }
        .tag-lightblue { color: #6cb6ff; }
        #welcome {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100%;
            text-align: center;
        }
        .hidden { display: none; }
        #loading { text-align: center; padding: 40px; color: var(--text-secondary); }
        .jump-target {
            color: #f0883e;
            cursor: pointer;
            font-weight: bold;
            text-decoration: underline;
        }
        .instruction.highlighted {
            background: rgba(240, 136, 62, 0.15);
            border-left: 3px solid #f0883e;
        }
    </style>
</head>
<body>
    <div id="sidebar">
        <div id="search-container">
            <input type="text" id="search-input" placeholder="Search dialogues or IDs...">
        </div>
        <div id="script-list">
            $sidebarItems
        </div>
    </div>
    <div id="main-content">
        <div id="welcome">
            <h1>Digimon World Script Dashboard</h1>
            <p>Select a script from the sidebar to view its logic and dialogues.</p>
            <p style="color: var(--text-secondary)">${manifest.entries.size} scripts indexed</p>
        </div>
        <div id="loading" class="hidden">Loading...</div>
        <div id="script-view" class="hidden"></div>
    </div>

    <script>
        const itemsData = $itemsJson;
        const triggersData = $triggersJson;
        const scriptsDir = "scripts";

        function escapeHtml(text) {
            return text ? text.toString()
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;") : "";
        }

        function formatDialogue(text) {
            return escapeHtml(text)
                .replace(/&lt;YELLOW&gt;/g, '<span class="tag-yellow">')
                .replace(/&lt;LIGHTBLUE&gt;/g, '<span class="tag-lightblue">')
                .replace(/&lt;WHITE&gt;/g, '</span>')
                .replace(/&lt;ORANGE&gt;/g, '<span style="color:#f0883e">')
                .replace(/&lt;GREEN&gt;/g, '<span style="color:#3fb950">')
                .replace(/&lt;PLAYER&gt;/g, '<span style="color:#f0883e">[PLAYER]</span>')
                .replace(/&lt;DIGIMON&gt;/g, '<span style="color:#f0883e">[DIGIMON]</span>')
                .replace(/\\n/g, '<br>');
        }

        function formatArgs(instr) {
            const args = instr.args || [];
            return args.map((arg, index) => {
                if (typeof arg === 'string' && arg.startsWith('trigger(')) {
                    let match = arg.match(/trigger\((\d+)\)/);
                    if (match && triggersData[match[1]]) {
                        return arg.replace(match[0], 'trigger(<span class="tag-yellow">' + triggersData[match[1]] + '</span>)');
                    }
                }
                if ((instr.opcode === 'setTrigger' || instr.opcode === 'unsetTrigger') && triggersData[arg]) {
                    return '<span class="tag-yellow" title="ID: ' + arg + '">' + triggersData[arg] + '</span>';
                }
                if (typeof arg === 'string' && arg.startsWith('item(')) {
                    let match = arg.match(/item\((\d+)\)/);
                    if (match && itemsData[match[1]]) {
                        return arg.replace(match[0], 'item(<span class="tag-lightblue">' + itemsData[match[1]] + '</span>)');
                    }
                }
                // Highlight jump targets
                if (instr.opcode === 'jumpTo' || instr.opcode === 'callRoutine') {
                    return '<span class="jump-target" onclick="jumpToAddress(' + arg + ')">' + arg + '</span>';
                }
                // Highlight 'then XXXX' at end of if
                if (instr.opcode === 'if' && !isNaN(arg) && args.indexOf(arg) === args.length - 1) {
                    return '→ <span class="jump-target" onclick="jumpToAddress(' + arg + ')">' + arg + '</span>';
                }
                return escapeHtml(arg);
            }).join(' ');
        }

        function renderScript(script) {
            let content = '<h2>' + escapeHtml(script.info) + '</h2>';
            for (const sectionId in script.sections) {
                const section = script.sections[sectionId];
                content += '<div class="section">';
                content += '<div class="section-header">Section ' + sectionId + '</div>';
                (section.instructions || []).forEach(instr => {
                    const isDialogue = instr.opcode === 'showTextbox';
                    let html = '<span class="instr-addr">' + instr.address + '</span>';
                    html += '<span class="instr-op">' + escapeHtml(instr.opcode) + '</span>';
                    if (isDialogue) {
                        html += '<div class="dialogue-box">' + formatDialogue((instr.args || [])[0] || '') + '</div>';
                    } else {
                        html += '<span>' + formatArgs(instr) + '</span>';
                    }
                    content += '<div class="instruction" id="instr-' + instr.address + '">' + html + '</div>';
                });
                content += '</div>';
            }
            return content;
        }

        async function loadScript(id, fileName) {
            document.querySelectorAll('.script-item').forEach(el => el.classList.remove('active'));
            const item = document.getElementById('script-item-' + id);
            if (item) item.classList.add('active');

            const welcome = document.getElementById('welcome');
            const loading = document.getElementById('loading');
            const view = document.getElementById('script-view');

            welcome.classList.add('hidden');
            view.classList.add('hidden');
            loading.classList.remove('hidden');

            try {
                const response = await fetch(scriptsDir + '/' + fileName);
                if (!response.ok) throw new Error('Failed to load ' + fileName);
                const script = await response.json();
                view.innerHTML = renderScript(script);
                view.classList.remove('hidden');
            } catch (e) {
                view.innerHTML = '<p style="color: var(--danger)">Error loading script: ' + e.message + '</p>';
                view.classList.remove('hidden');
            } finally {
                loading.classList.add('hidden');
            }
        }

        document.getElementById('search-input').addEventListener('input', function(e) {
            const query = e.target.value.toLowerCase();
            document.querySelectorAll('.script-item').forEach(item => {
                item.classList.toggle('hidden', !item.innerText.toLowerCase().includes(query));
            });
        });
        function jumpToAddress(address) {
            const target = document.getElementById('instr-' + address);
            if (target) {
                // Remove previous highlight
                document.querySelectorAll('.instruction.highlighted').forEach(el => el.classList.remove('highlighted'));
                target.classList.add('highlighted');
                target.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    </script>
</body>
</html>
        """.trimIndent()

        outputFile.writeText(html)
        println("Dashboard saved to ${outputFile.absolutePath}")
    }

    private fun escapeHtml(text: String): String =
        text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}