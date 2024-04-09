<script lang="ts">
    import { writable } from 'svelte/store';
  
    // Définition du type de fichier
    interface File {
      name: string;
      size: number;
      pieceSize: number;
    }


    let fileName = "";
    let fileSize = 0;
    let pieceSize = 0;

    // Store contenant la liste des fichiers
    const files = writable<File[]>([]);
  
    // Fonction pour créer un nouveau fichier
    function createFile(name: string, size: number, pieceSize: number) {
      const newFile: File = { name, size, pieceSize };
      files.update(existingFiles => [...existingFiles, newFile]);
    }
  
    // Fonction pour supprimer un fichier
    function deleteFile(name: string) {
      files.update(existingFiles => existingFiles.filter(file => file.name !== name));
    }
  </script>
  
  <main>
    <!-- Interface pour créer un nouveau fichier -->
    <div class="file-form">
      <input type="text" placeholder="Nom du fichier" bind:value={fileName}>
      <input type="number" placeholder="Taille du fichier (en octets)" bind:value={fileSize}>
      <input type="number" placeholder="Taille des pièces (en octets)" bind:value={pieceSize}>
      <button on:click={() => createFile(fileName, fileSize, pieceSize)}>Créer un fichier</button>
    </div>
  
    <!-- Liste des fichiers -->
    <div class="file-list">
      {#each $files as file}
        <div class="file-item">
          <p>Nom: {file.name}</p>
          <p>Taille: {file.size} octets</p>
          <p>Taille des pièces: {file.pieceSize} octets</p>
          <button on:click={() => deleteFile(file.name)}>Supprimer</button>
        </div>
      {/each}
    </div>
  </main>
  
  <style>
    main {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px;
    }
  
    .file-form, .file-list {
      margin-bottom: 20px;
    }
  
    .file-item {
      border: 1px solid #ccc;
      border-radius: 5px;
      padding: 10px;
      margin-bottom: 10px;
    }
  
    button {
      background-color: #007bff;
      color: #fff;
      border: none;
      padding: 5px 10px;
      cursor: pointer;
    }
  
    button:hover {
      background-color: #0056b3;
    }
  </style>
  