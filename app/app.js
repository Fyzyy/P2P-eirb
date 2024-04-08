

document.addEventListener('DOMContentLoaded', function() {
    const filesList = document.getElementById('files');
    const commandInput = document.getElementById('commandInput');
    const output = document.getElementById('output');
    const submitCommandBtn = document.getElementById('submitCommand');

    // Dummy file list (replace with actual data from the peer)
    const myFiles = ['file1.txt', 'file2.jpg', 'file3.mp3'];

    // Display files in the list
    myFiles.forEach(file => {
        const li = document.createElement('li');
        li.textContent = file;
        filesList.appendChild(li);
    });

    // Event listener for submitting commands
    submitCommandBtn.addEventListener('click', function() {
        const command = commandInput.value.trim();
        executeCommand(command);
    });

    // Function to execute commands
    function executeCommand(command) {
        // Dummy function for demo purposes (replace with actual command execution)
        output.innerHTML = `<p>Command: ${command}</p>`;
        commandInput.value = '';
    }
});
