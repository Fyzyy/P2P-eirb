<script lang="ts">
  import Terminal from './lib/Terminal.svelte'
  import TrackerInfo from './lib/TrackerInfo.svelte'
  import File from './lib/File.svelte'

  const adress = undefined
  const port = undefined
  const url = `http://${adress}:${port}`

</script>

<main>
  <div class="container">
    <TrackerInfo/>
    <File/>
    <Terminal/>
  </div>
</main>

<style>
  main {
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: rgb(189, 185, 230);
    width:100%;
  }

  .container {
    flex-direction: column;
    align-items: center;
    padding: 20px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0px 0px 10px 0px rgba(0,0,0,0.2);

  }
</style>
