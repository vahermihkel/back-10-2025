#!/bin/bash

# Prompt the user for confirmation
read -p "Do you have PostgreSQL installed? (y/n): " confirmation

# Check the response
if [[ "$confirmation" == "y" || "$confirmation" == "Y" ]]; then
    echo "Proceeding..."
else
    echo "Operation canceled."
    exit 1
fi

# Variables
FRONTEND_DIR="../veebipood-frontend"
DB_USER="postgres"
DB_HOST="localhost"
DB_PORT="5432"
APP_PROPERTIES="../veebipood/src/main/resources/application.properties"
# Step 1: Ask for the PostgreSQL password
echo "Please enter your PostgreSQL password: "
read -s DB_PASSWORD    # -s for silent input (password will not be shown)

echo "Please enter your database name: "
read DB_NAME

# Step 2: Install Node Modules
echo "Installing node_modules for front-end..."
cd $FRONTEND_DIR
npm install

# Step 3: Create PostgreSQL Database
echo "Creating PostgreSQL database..."
# Log in to PostgreSQL and create the database, passing the password using PGPASSWORD
export PGPASSWORD=$DB_PASSWORD
psql -U $DB_USER -h $DB_HOST -p $DB_PORT -c "CREATE DATABASE $DB_NAME;"

# Step 4: Insert database credentials into application.properties
echo "Updating application.properties with database credentials..."

# Check if application.properties exists
if [ -f "$APP_PROPERTIES" ]; then
    # Write database properties into the application.properties file
   cat > $APP_PROPERTIES <<EOL
spring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME
spring.datasource.username=$DB_USER
spring.datasource.password=$DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
EOL

    echo "application.properties updated successfully."
else
    echo "Error: application.properties file not found."
fi

# Unset the password for security
unset PGPASSWORD

echo "Script execution completed."