-- Load the input
InputData = LOAD 'input';
WordsData = LOAD 'words' as (words:chararray);

-- Generate word tokens
Tokens = foreach InputData generate TOKENIZE((chararray)$0) as word;
Tokens = FOREACH Tokens GENERATE FLATTEN(word) as word;
Tokens = foreach Tokens generate FLATTEN(REGEX_EXTRACT_ALL(LOWER(word),'.*?([a-z]+).*?')) as word;

-- LEFT JOIN
FilteredData = JOIN WordsData BY words LEFT, Tokens BY word;

-- Group it
Groups = GROUP FilteredData BY words;
Result = FOREACH Groups GENERATE group, COUNT($1.word);

-- Filter does not give the count for words that are not found
-- FilteredData = FILTER Tokens BY (word matches '.*hackathon.*' OR word matches '.*dec.*' OR word matches '.*chicago.*' OR word matches '.*java.*');

DUMP Result;
