<script>
	export let name;

	async function loadData(){
		const response = await fetch("http://localhost:8080/test?param=jud");

		name = await response.text();
	}
	loadData();
</script>

<main>
	<h1>Hello {name} in Svelte!</h1>
</main>

<style>
	main {
		text-align: center;
		padding: 1em;
		max-width: 240px;
		margin: 0 auto;
	}

	h1 {
		color: #ff3e00;
		text-transform: uppercase;
		font-size: 4em;
		font-weight: 100;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}
</style>