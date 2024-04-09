<script lang="ts">
    import { writable } from 'svelte/store';
  
    // Créer un store pour gérer l'état de la connexion
    const isConnected = writable(false);
  
    // Fonction pour vérifier la connexion au tracker
    async function checkConnection() {
      try {
        const response = await fetch('http://localhost:8080/');
  
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
  
        // Analyser la réponse JSON
        const data = await response.json();
  
        // Mettre à jour l'état de la connexion en fonction de la réponse du tracker
        isConnected.set(data.isConnected);
      } catch (error) {
        console.error('Error:', error);
      }
    }
  
    // Appeler la fonction de vérification de la connexion au chargement du composant
    checkConnection();
</script>
  
<div class:connected={$isConnected}>
    <p>{#if $isConnected} Connected to tracker! {:else} Not connected to tracker! {/if}</p>
</div>

<style>
    div {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        width: 90px;
        height: 90px;
        border-radius: 60%;
        background-color: red;
        text-size-adjust: 10px;
    }

    .connected {
        background-color: green;
    }
</style>
