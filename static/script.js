document.getElementById('processButton').addEventListener('click', () => {
    fetch('api/process-text')
        .then(response => response.json())
        .then(data => {
            const resultDiv = document.getElementById('result');
            if (data.status === 'success') {
                const parsedData = data.data;
                resultDiv.innerHTML = '<h2>Asset Data:</h2>';
                for (const [asset, datePricePairs] of Object.entries(parsedData)) {
                    resultDiv.innerHTML += `<h3>${asset}</h3>`;
                    datePricePairs.forEach(([date, price]) => {
                        resultDiv.innerHTML += `<p>Date: ${date}, Price: ${price}</p>`;
                    });
                }
            } else {
                resultDiv.innerHTML = `<p>Error: ${data.message}</p>`;
            }
        })
        .catch(error => {
            document.getElementById('result').innerHTML = `<p>Error: ${error.message}</p>`;
        });
});
