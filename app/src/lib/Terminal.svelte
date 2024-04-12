<script lang="ts">
    let commandHistory: string[] = [];
    let currentCommand = "";
    let output: { text: string; type: "user" | "server" }[] = [];

    function executeCommand() {
        fetch("http://localhost:8080", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ command: currentCommand }),
        })
            .then((response) => response.json())
            .then((data) => {
                output = [...output, { text: data.output, type: "server" }];
            })
            .catch((error) => {
                output = [...output, { text: `Error: ${error.message}`, type: "server" }];
            });

        output = [...output, { text: `${currentCommand}`, type: "user" }];

        // Ajouter la commande à l'historique
        commandHistory = [...commandHistory, currentCommand];
        currentCommand = "";
    }

    function handleKeyDown(event: KeyboardEvent) {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            executeCommand();
        }
    }
</script>

<div class="terminal">
    {#each output as { text, type }}
        <p class={type === "server" ? "server" : "user"}>{text}</p>
    {/each}
    <div class="input">
        <textarea
            bind:value={currentCommand}
            on:keydown={handleKeyDown}
            placeholder="Enter command..."
        ></textarea>
        <button on:click={executeCommand}>Submit</button>
    </div>
</div>

<style>
    .terminal {
        font-family: monospace;
        border: 1px solid #000;
        border-radius: 5px;
        overflow: hidden;
        display: flex;
        flex-direction: column;
        background-color: #000;
        color: #fff;
        height: 400px;
        padding: 10px;
        width: auto;
    }

    .server, .user {
        margin-left: 0;
    }
    .terminal p {
        margin: 0;
        margin-right: auto;
    }

    .user::before {
        content: "< ";
        color: #0f0;
    }

    .server::before {
        content: "> ";
        color: #00f;
    }

    .input {
        display: flex;
        align-items: center;
        padding: 5px;
        background-color: #f2f2f2;
        margin-top: auto;
        border-radius: 5px;
        width: 99%;
        margin-right: 5px;
    }

    textarea {
        flex: 1;
        resize: none;
        border: none;
        padding: 5px;
        font-family: monospace;
        font-size: 14px;
    }

    button {
        background-color: #007bff;

        border: none;
        padding: 5px 10px;
        margin-left: 4px;
        cursor: pointer;
    }

    button:hover {
        background-color: #0056b3;
    }
</style>
