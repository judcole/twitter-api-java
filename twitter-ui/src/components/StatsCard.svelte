<!--
    Svelte component to display Twitter statistics
-->

<script lang="ts">
    export let dailyTweets, hourlyTweets, lastUpdated, status, topHashtagCounts, topHashtags, topHashtagsSize,
        totalHashtags, totalTweets, tweetQueueCount

    lastUpdated = lastUpdated ? lastUpdated.substring(0, 19).replace('T', ' ') : "";
</script>

<div class="container stats">
    <div class="text-center">
        <div class="container col-6">
            <table>
                <tr>
                    <th>Statistic</th>
                    <th>Value</th>
                </tr>
                <tr>
                    <td>Last updated</td>
                    <td id="last-updated">
                        {lastUpdated}
                    </td>
                </tr>
                <tr>
                    <td>Total hashtags</td>
                    <td id="total-hashtags">{totalHashtags}</td>
                </tr>
                <tr>
                    <td>Total tweets</td>
                    <td id="total-tweets">{totalTweets}</td>
                </tr>
                <tr>
                    <td>Daily tweets</td>
                    <td id="daily-tweets">{dailyTweets}</td>
                </tr>
                <tr>
                    <td>Hourly tweets</td>
                    <td id="hourly-tweets">{hourlyTweets}</td>
                </tr>
                <tr>
                    <td>Tweets in queue</td>
                    <td id="queued-tweets">{tweetQueueCount}</td>
                </tr>
                <tr>
                    <td>Last status</td>
                    <td id="last-status">{status}</td>
                </tr>
            </table>
            <table>
                <tr>
                    <th>Hashtag rank</th>
                    <th>Hashtag</th>
                    <th>Usage count</th>
                </tr>
                {#each topHashtags as hashtag, i}
                    <tr>
                        <td>{i + 1}</td>
                        <td id="hashtag-{i}">{hashtag}</td>
                        <td id="hashtag-count-{i}">{topHashtagCounts[i]}</td>
                    </tr>
                {/each}
            </table>
        </div>
    </div>
</div>

<style>
    table {
        font-family: arial, sans-serif;
        border-collapse: collapse;
        width: 100%;
        margin-top: 40px;
    }

    td, th {
        border: 1px solid #dddddd;
        text-align: left;
        padding: 8px;
    }

    tr:nth-child(even) {
        background-color: #dddddd;
    }
</style>
