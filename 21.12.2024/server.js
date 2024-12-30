const express = require('express');
const bodyParser = require('body-parser');
const { exec } = require('child_process');
const path = require('path');

const app = express();
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));
app.set('view engine', 'ejs');

// Route to render the page
app.get('/', (req, res) => {
    res.render('index');
});

// Helper function to run commands sequentially
function runCommand(command) {
    return new Promise((resolve, reject) => {
        exec(command, (error, stdout, stderr) => {
            if (error) {
                console.error(`Error executing command: ${error.message}`);
                reject(error);
            }
            if (stderr) {
                console.error(`Command stderr: ${stderr}`);
            }
            console.log(`Command stdout: ${stdout}`);
            resolve(stdout);
        });
    });
}

// Route to execute the sequence of Docker commands
app.post('/run-script', async (req, res) => {
    const commands = [
        'docker build -t main-test .',
        'docker run --name main-test-container main-test',
        'docker cp main-test-container:/app/Prices3.txt .',
        'docker rm main-test-container'
    ];

    try {
        // Run each command sequentially
        for (let command of commands) {
            await runCommand(command);  // Wait for each command to finish before moving to the next
        }
        res.send({ message: 'Script executed successfully, Docker commands completed.' });
    } catch (error) {
        res.status(500).send({ error: 'Error running Docker commands' });
    }
});

// Start server
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});
