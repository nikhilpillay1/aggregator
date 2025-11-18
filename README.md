# Aggregator 🐊

A financial transaction aggregator that allows you to upload a CSV of your transaction history (most financial organizations allow you to export your transactions as CSV), that uses ✨AI ✨ to aggregate them - along with an extensive API to query the aggregated data.

## How it works

There are 4 relevant parts of the application, that are each created as separate docker images.

1) The API: The heart of the system. Exposes an endpoint that takes in a CSV file, parses those CSVs into Transaction entities, and sends the descriptions of those entities to an LLM to categorize.

2)  The LLM. Ideally, this would be a powerful model running in a data centre, accessed with an API key - but in order to eliminate a dependency on anything that isn't in this codebase, the application instead uses Ollama to run a model locally, within its Docker container. The model that provided the best balance of being small, easy to run, and fairly accurate was `mistral`. 

3) The database. A standard Postgres database, nothing fancy. Hibernate's DDL-auto property is set to `create-drop`, for development purposes, so the schema is created when the application runs, and deleted afterwards.

4) The UI. This is an Angular application, but it exists purely as a way for someone to view what the API can do - the code quality is messy and rushed.

## Setup

1) Firstly, clone the repository. If you already have Git installed, just open up a terminal and 
   
   `git clone https://github.com/nikhilpillay1/aggregator`
   
   Then enter this directory.
   
2) Ollama runs significantly faster on a PC with a dedicated graphics card. If running the app on a computer with a GPU, open up the file `docker-compose.yml` in a text editor, and navigate to line 40, which should be commented out:
   
deploy:  
  resources:  
    reservations:  
      devices:  
        - driver: nvidia  
          count: all  
          capabilities: [ gpu ]

If you have a dedicated Nvidia GPU, uncomment these lines. If you have a dedicated AMD GPU, uncomment these lines, and replace `driver: nvidia` with `driver: amd` (I have only confirmed this works on Nvidia GPUs). If you don't have a dedicated graphics card, just leave these lines commented out - the application will still work, it'll just take longer to aggregate transactions. 

3) Open up a new terminal in the project root, and run
   
   `docker compose up -d`
   
   this will take a while, as it needs to download all the required images, including Ollama. (~2GB download)
   
4) Once this process is done, the front-end and back-end should be built and running, and the database and Ollama should also be running - but without a model, the application won't be able to aggregate transactions. In order to download the model, run:
   
   `docker exec ollama ollama pull mistral`
   
   Ollama has to download this model, so prepare for a ~4.5GB download.

3) Now the application is ready! Open up a browser and navigate to:
   
   `http://localhost/`
   
## Usage

The application has built-in support for the CSVs that can be exported from a few different financial institutions, including Capitec Bank, FNB and Paypal. If you want to extract any of these, just select the source on the dropdown, and upload the file with the buttons. (Example CSVs are provided in the project root) The application should (after aggregating the transactions with AI) display the transactions on the table.

But it's also easy to add support for more financial institutions. Just navigate to:

`aggregator\api\aggregator-api\src\main\resources\application.yaml`

And scroll down to the config csv-source. You can add any new transaction source by copying any of the existing sources, and replacing the configs.

for header-line, just paste the entire header line of your CSV within the single quotes.

for date-key, use whatever header in your CSV indicates the "date" of a transaction.

for amount-key, use whatever header in your CSV indicates the "amount" of a transaction. In the event there are multiple relevant keys here (for example, Money In, Money Out, and Fee), just paste all the relevant keys separated by commas (see Capitec for an example), and the application will calculate the total.

for description-key, use whatever header in your CSV best indicates what type of transaction took place. In the event there are multiple relevant keys here (Description, Category) just paste all the relevant keys separated by commas (see Capitec for an example), and the application will concatenate them together and send the resulting String to the AI model.

for date-format, use whatever format dates are stored with in your CSV.

## Troubleshooting

These are a few issues that you might experience, and how to solve them.

Issue: The application is categorizing every transaction as "OTHER".
Solution: Either Ollama isn't running, or doesn't have the `mistral` model downloaded.

Issue: The application isn't categorizing some of my transactions very well.
Solution: AI models, especially at this size, are inherently indeterministic, so it's always possible for errors to occur. This application is just meant to save time that would be spent categorizing transactions manually, but your financial information should always be reviewed by a person.

Issue: I added a new data source, but extraction is failing. 
Solution: While the application should be able to work with CSVs of various different formats, it's possible for an edge case in formatting that the application wasn't prepared for.

Issue: I added a new data source, but I can't select it on the front-end.
Solution: Data sources are extracted from `application.yaml`, which the application does not re-check during runtime, so you might need to restart the API with `docker compose build api` then `docker compose up api -d` for a new data source to be picked up. For an actual production application, this would definitely be stored in the database instead of being stored in `application.yaml`, but I kept them in the file so that it is visually easy to see how the functionality of the system can be extended to more data sources.

Issue: I want more categories.
Solution: More categories can be added in the `enum` `TransactionCategory`. If you do this,  restart the API with `docker compose build api` then `docker compose up api -d` for the new categories to be picked up. This was also designed this way to make it easy to see where the categories are coming from, and should also be stored in the database for a production application.

   

   
