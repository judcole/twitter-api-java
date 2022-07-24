<script lang="ts">
    import StatsCard from "./components/StatsCard.svelte";

    export let jsonResponse: string;

    async function loadData() {
        const response = await fetch("http://localhost:8080/getStats");

        let json = await response.json();

        jsonResponse = JSON.stringify(json, null, 2);

        return json;
    }

    let promisedJson = loadData();

    // https://www.w3schools.com/jsref/jsref_tolocalestring.asp
    const dayName = new Date().toLocaleDateString('en', {
        weekday: "long"
    })
</script>

<main>
<!--    <h2>{jsonResponse}</h2>-->

    <h2 class="title">Welcome to our Twitter SampledStream Application on this fine {dayName}</h2>

    {#await promisedJson then json}
        <StatsCard {...json}
        class="spacer"/>
    {/await}

    <hr class="spacer"/>
    <p>Learn more about <a href="https://www.judcole.com">Jud Cole</a>.</p>
</main>

<style>
    main {
        text-align: center;
        padding: 1em;
        max-width: 240px;
        margin: 0 auto;
    }

    .spacer {
        margin-top: 60px;
    }

    @media (min-width: 640px) {
        main {
            max-width: none;
        }
    }
</style>