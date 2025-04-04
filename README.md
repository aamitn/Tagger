Tagger: Tag Replacement Tool
============================

Tagger is a versatile Java application designed to simplify the process of replacing tags within text content. It provides a user-friendly GUI that allows users to interactively replace tags in a structured manner. The application supports a wide range of features including tag extraction, tag comment retrieval, and advanced tag replacement options.

Features
--------

-   Interactive GUI: Tagger offers an intuitive graphical user interface that allows users to seamlessly navigate and perform tag replacement operations.

-   Tag Extraction: The application intelligently extracts tags from the provided content, making it easy for users to identify and manage tags.

-   Tag Comment Retrieval: Tagger fetches tag comments associated with each tag, providing context to the user during the replacement process.

-   Advanced Tag Replacement: Users can replace tags with customized text, including the ability to handle nested tags and optional tag content.

-   Configuration Support: Tagger reads configuration settings from a JSON file, allowing users to easily customize database connections, table names, and more.

-   Save and Load Data: Users can save and load data, enabling seamless work sessions across different instances of the application.

-   Data Persistence: The application stores tag replacement history, allowing users to review and track previous tag replacements.

-   Error Handling: Tagger provides informative error messages and visual alerts, including an error message for failed database connections.

-   Version Control: The project maintains a comprehensive version history, making it easy to track changes and updates.

Usage
-----

1.  Launch the application.
2.  Select the `mo_name` from the dropdown.
3.  Click the "Process" button to begin the tag replacement process.
4.  For each tag, the application presents a replacement dialog allowing the user to customize replacement text.
5.  Optional: Users can save and load data using the provided buttons in the GUI.

Getting Started
---------------

1.  Clone this repository:

```sh
git clone https://github.com/aamitn/Tagger.git
```

2.  Navigate to the project directory:

```sh
cd Tagger
```

3.  Set Up the Database: 
 - Configure the database connection credentials in the `trtconfig.json` file.
 - You may need to set up your preferred database system (e.g., MySQ/PostgreSQL etc any db having suppoorted JDBC drivers ) and create the necessary tables before running the application. 
 - You can create the database schema by running the included `tagger.sql` file . (Schema is provided for MySQL DB)

4.  Build and run the application using your preferred Java IDE or command-line tools. For example, using the command-line:

```sh
    javac TagReplacementToolApp.java
    java TagReplacementToolApp
```


Database Schema (Generic , can be adopted for any relational DB)
-------------

### Table: `table1`

| Column Name  | Data Type   | Constraints               | Description                          |
|-------------|------------|--------------------------|----------------------------------|
| `mo_name`   | VARCHAR(15) | PRIMARY KEY, NOT NULL     | Unique identifier for each record. |
| `mo_content` | TEXT       | NULLABLE                  | Content with embedded tags.      |
| `mo_tags`   | TEXT       | NULLABLE                  | Comma-separated list of associated tags. |

### Table: `table2`

| Column Name  | Data Type   | Constraints               | Description                          |
|-------------|------------|--------------------------|----------------------------------|
| `mo_tags`   | VARCHAR(15) | PRIMARY KEY, NOT NULL     | Unique identifier for each tag. |
| `tag_comment` | TEXT       | NULLABLE                  | Description or comment about the tag. |






Configuration
-------------

Configuration settings are stored in the `trtconfig.json` file. Modify this file to adjust database connections, table names, and other parameters. This file should be located on the root of C: drive in Windows

Contributing
------------

Contributions to this project are welcome. Feel free to submit bug reports, feature requests, and pull requests.

License
-------

This project is licensed under the [MIT License](https://mit-license.org).
