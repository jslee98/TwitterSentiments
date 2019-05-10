# TwitterSentiments
By Jeffrey Lee

**Introduction**
For this project, we focused on collecting publicly available tweets from Twitter, analyzing them, and visualizing the results. To do this, we used a series of Java applications with Twitter4J to connect to Twitter’s API and build the maps. We also used GATE and the TwitIE plugin for GATE to analyze and extract information from the text. We also used a gazetteer to judge the sentiment of each tweet. For my project, I was given the area of New York City to focus on.

**Data Collection**
To collect data for this project, I used two different methods from the Twitter4J package. The first was a simple query that acquired recent tweets within a radius from a certain point. For my purposes, I fetched up to 100 tweets at a time within a 3-mile radius of Midtown, Manhattan (40.7549 ,-73.9840). This method was effective if I wanted to fetch tweets at a certain time of day, and it can be seen in the Java class SearchAroundLocation.

The next method I used was the bounding box program we saw in class. I chose the boundaries of (-74,40) and (-73,41) to enclose New York City based on the website we saw in class. This program was a tweet listener and can run continuously until stopped, saving every tweet from within the boundary that occurred since it began running. This method was useful if I wanted to extract data over a longer period of time and not have to manually collect it every 5-10 minutes. The functionality can be seen in the classes SearchByLocation and MyStatusListener. In the end, I collected 2,340 tweets for analysis.

**Information Extraction**
Data extraction in this project was done entirely in GATE and preprocessed before running any Java application. We used the TwitIE GATE plugin to extract information and annotate each individual tweet. This package is specialized for information extraction of social media posts, and accounts for abbreviated words, emojis, and more.

After running TwitIE on my corpus, I needed to use an ANNIE Gazetteer to judge the sentiment of key words in the tweets. To do this, I wrote the Java utility PrepGazetteer to process the XML dictionaries that were provided to us in the lab. The most complicated part of this process was only including positive words with polarity greater than 0.5 and negative words with polarity less than -0.5. However, after testing different methods of document extraction, I was able to compile four positive and negative word lists to supply our gazetteer with.

**Sentiment Computation**
Computing the sentiment of a tweet was fairly easy after the gazetteer annotated each file. For each tweet, I would keep a count of positive and negative sentiment words. I also set a filter that would only count a positive word if it was in the same language as the tweet, and the same for negative. This helps us avoid miscounting words in different languages. The tweet language was taken from the original markups, while the sentiment language came from our gazetteer. At the end of the text, the final sentiment would be marked positive, negative, or neutral depending on the count of each annotation.

**Map Creation**
Creating the map was a straightforward task given the outline provided by the instructor. I discovered that the supplied Java program added JSON objects to the map template, so to include extra fields such as the number of organizations, people, locations, or links used, all I had to do was add to the Java object being written to the file. Then, in the HTML template, I edited the callback function to display a box with more fields to meet the requirements. I decided on using green to denote positive sentiments, blue to denote neutral sentiments, and red to denote negative sentiments. These colors are all easily distinguishable and already hold similar meaning in many user’s minds. For the heat map, not much adjustment was needed to replicate the functionality of the example. Both maps needed to be centered on Midtown, Manhattan, to properly show the data.

![maps](https://i.imgur.com/VFanoFR.png)
