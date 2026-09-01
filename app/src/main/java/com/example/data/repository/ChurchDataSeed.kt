package com.example.data.repository

import com.example.data.local.AnnouncementEntity
import com.example.data.local.PrayerRequestEntity
import com.example.data.model.*

object ChurchDataSeed {

    val pastors = listOf(
        Pastor(
            id = "pastor_david",
            name = "Dr. David Sterling",
            title = "Senior Pastor",
            bio = "Dr. David Sterling has served Grace Church for over 16 years. He holds a Ph.D. in New Testament theology from Oxford and is devoted to gospel-centered expository preaching, theological discernment, and shepherding families into deeper faith in Christ.",
            education = "Ph.D. New Testament (Oxford University) • M.Div. (Trinity)",
            yearsOfMinistry = "16+ Years at Grace Church",
            officeLocation = "Main Sanctuary, Suite 101",
            email = "pastor.david@gracechurch.org",
            phone = "(555) 234-8901",
            officeHours = "Tue & Thu: 9:00 AM – 3:00 PM",
            availabilityStatus = "Available for Guidance",
            specialty = listOf("Expository Preaching", "Theological Counseling", "Pastoral Mentorship", "Biblical Discernment"),
            favoriteScripture = "Romans 8:38-39",
            guidancePromptStarters = listOf(
                "Seeking clarity on God's calling in my career",
                "Questions regarding scripture passage or doctrine",
                "Personal prayer for wisdom in leadership"
            )
        ),
        Pastor(
            id = "pastor_sarah",
            name = "Pastor Sarah Jenkins",
            title = "Executive & Pastoral Care Pastor",
            bio = "Pastor Sarah oversees pastoral care and compassionate family ministry. With an M.Div. and licensed Christian counseling certification, she brings heartfelt empathy and biblical truth to people walking through grief, marital hurdles, or life transitions.",
            education = "M.Div. (Gordon-Conwell) • Licensed Christian Counselor (LPC)",
            yearsOfMinistry = "12+ Years Pastoral Care",
            officeLocation = "Pastoral Care Center, Room 204",
            email = "sarah.jenkins@gracechurch.org",
            phone = "(555) 234-8902",
            officeHours = "Mon, Wed & Fri: 10:00 AM – 4:00 PM",
            availabilityStatus = "Accepting Counseling Requests",
            specialty = listOf("Biblical Counseling", "Marriage & Family", "Grief & Healing", "Emotional Health"),
            favoriteScripture = "Psalm 34:18",
            guidancePromptStarters = listOf(
                "Need counseling for a family or relationship trial",
                "Navigating grief and seeking God's peace",
                "Confidential pastoral prayer and support"
            )
        ),
        Pastor(
            id = "pastor_marcus",
            name = "Pastor Marcus Hayes",
            title = "Youth & Young Adults Pastor",
            bio = "Marcus is deeply passionate about discipling the next generation in deep theological truth and cultural discernment. He organizes weekly collegiate prayer networks, student mentorships, and local community service initiatives.",
            education = "M.A. Discipleship & Culture (Wheaton College)",
            yearsOfMinistry = "8+ Years Youth & Young Adult Ministry",
            officeLocation = "Youth Ministry Loft, 3rd Floor",
            email = "marcus.hayes@gracechurch.org",
            phone = "(555) 234-8903",
            officeHours = "Wed & Thu: 1:00 PM – 6:00 PM",
            availabilityStatus = "Available for Discipleship",
            specialty = listOf("Young Adults Discipleship", "Campus Outreach", "Faith & Culture", "Vocation Discernment"),
            favoriteScripture = "1 Timothy 4:12",
            guidancePromptStarters = listOf(
                "Navigating faith and doubts at college/work",
                "Looking for young adult fellowship and mentorship",
                "Discerning next steps after graduation"
            )
        ),
        Pastor(
            id = "pastor_elena",
            name = "Pastor Elena Vance",
            title = "Worship & Prayer Ministries Pastor",
            bio = "Elena leads Grace Church's worship teams and directs city-wide intercessory prayer networks. She mentors believers in liturgical prayer, personal devotional stillness, and deepening an intimate walk with the Holy Spirit.",
            education = "M.A. Worship Studies & Spiritual Formation (Regent College)",
            yearsOfMinistry = "10+ Years Worship & Intercession",
            officeLocation = "Worship Arts Suite, Studio B",
            email = "elena.vance@gracechurch.org",
            phone = "(555) 234-8904",
            officeHours = "Tue & Fri: 11:00 AM – 4:00 PM",
            availabilityStatus = "Available for Prayer & Spiritual Formation",
            specialty = listOf("Intercessory Prayer", "Spiritual Formation", "Worship Arts", "Contemplative Devotion"),
            favoriteScripture = "Psalm 27:4",
            guidancePromptStarters = listOf(
                "Seeking guidance to develop a deeper prayer life",
                "Requesting targeted intercessory prayer for healing",
                "Growing in spiritual disciplines and stillness"
            )
        ),
        Pastor(
            id = "pastor_jonathan",
            name = "Pastor Jonathan Miller",
            title = "Community Outreach & Missions Pastor",
            bio = "Pastor Jonathan leads Grace Church's local benevolence programs, homeless outreach, and global missionary partnerships. He loves helping church members discover and activate their spiritual gifts in service.",
            education = "M.Div. Urban Missiology (Biola University)",
            yearsOfMinistry = "9+ Years Urban Missions",
            officeLocation = "Community Outreach Annex, Room 108",
            email = "jonathan.miller@gracechurch.org",
            phone = "(555) 234-8905",
            officeHours = "Mon & Thu: 10:00 AM – 3:00 PM",
            availabilityStatus = "Available for Outreach Guidance",
            specialty = listOf("Local Missions", "Benevolence Care", "Spiritual Gifts Discovery", "Community Outreach"),
            favoriteScripture = "Micah 6:8",
            guidancePromptStarters = listOf(
                "How can I discover my spiritual gifts for ministry?",
                "Connecting with local community outreach projects",
                "Benevolence and support for a family in crisis"
            )
        )
    )

    val sermons = listOf(
        Sermon(
            id = "sermon_1",
            title = "Rooted in Grace: The Unshakable Foundation",
            pastorName = "Dr. David Sterling",
            pastorTitle = "Senior Pastor",
            seriesName = "Anchored in Christ",
            date = "Sunday Service • August 24",
            durationMinutes = 38,
            scriptureReference = "Ephesians 2:1-10",
            summary = "In this message, Pastor David examines Paul's letter to the Ephesians, demonstrating how God's unmerited favor transforms our identity from spiritual deadness to resurrected purpose in Christ.",
            keyPoints = listOf(
                "Grace is unearned gift, not wages for moral achievement.",
                "We are His workmanship (poiēma), created for good works prepared in advance.",
                "Living from acceptance rather than for acceptance brings true peace."
            ),
            studyNotes = "Ephesians 2 teaches us that our salvation is entirely founded upon God's rich mercy. When we realize we cannot save ourselves, anxiety turns to deep gratitude."
        ),
        Sermon(
            id = "sermon_2",
            title = "Peace in the Tempest: Trusting God When Life Crumbles",
            pastorName = "Pastor Sarah Jenkins",
            pastorTitle = "Pastoral Care",
            seriesName = "Songs in the Valley",
            date = "Midweek Gathering • August 20",
            durationMinutes = 32,
            scriptureReference = "Psalm 46:1-11",
            summary = "When circumstances shift like mountains sliding into the sea, God remains our refuge and present help. Pastor Sarah shares practical pastoral wisdom for anxiety.",
            keyPoints = listOf(
                "God is not a distant bystander in trial; He is an immediate refuge.",
                "'Be still and know' (raphah) means releasing our frantic grip on outcomes.",
                "The River of God makes glad the city of His people in every season."
            ),
            studyNotes = "Psalm 46 is historically the bedrock for Martin Luther's 'A Mighty Fortress'. Meditate on verse 10 throughout this week."
        ),
        Sermon(
            id = "sermon_3",
            title = "Kingdom Culture: Loving Your Neighbor in a Divided World",
            pastorName = "Pastor Marcus Hayes",
            pastorTitle = "Youth & Young Adults",
            seriesName = "The Sermon on the Mount",
            date = "Sunday Evening • August 17",
            durationMinutes = 35,
            scriptureReference = "Matthew 5:13-16",
            summary = "How do Christians act as salt and light today? Pastor Marcus unpacks how radical hospitality, truth spoken in love, and authentic community heal neighborhood isolation.",
            keyPoints = listOf(
                "Salt preserves and brings out the authentic flavor of God's goodness.",
                "Light exposes darkness gently, leading people home to the Father.",
                "Good works are visible signposts that glorify God, not ourselves."
            ),
            studyNotes = "Reflect on how your workplace or neighborhood can experience Christ's sacrificial love through your everyday presence."
        ),
        Sermon(
            id = "sermon_4",
            title = "The Power of Secret Prayer: Aligning Our Heart with Heaven",
            pastorName = "Pastor Elena Vance",
            pastorTitle = "Worship & Prayer Ministries",
            seriesName = "Secret Place Devotions",
            date = "Prayer Vigil • August 12",
            durationMinutes = 29,
            scriptureReference = "Matthew 6:5-15",
            summary = "True intimacy with the Father begins when the door is shut to human applause. Elena leads a practical guide into contemplative prayer and intercession.",
            keyPoints = listOf(
                "Prayer is not informing God of what He doesn't know; it is conforming our desires to His.",
                "The Lord's Prayer gives us a template for adoration, petition, and forgiveness.",
                "A quiet heart hears the gentle whisper of the Holy Spirit."
            ),
            studyNotes = "Try setting aside 15 minutes of silence before making your requests known to God each morning."
        )
    )

    val devotionals = listOf(
        Devotional(
            id = "dev_1",
            date = "Today • August 28",
            title = "Abiding in the True Vine",
            authorPastor = "Dr. David Sterling",
            scriptureRef = "John 15:4-5",
            scriptureText = "Remain in me, as I also remain in you. No branch can bear fruit by itself; it must remain in the vine. Neither can you bear fruit unless you remain in me. I am the vine; you are the branches. If you remain in me and I in you, you will bear much fruit; apart from me you can do nothing.",
            reflectionText = "Our culture measures worth by nonstop production, output, and speed. Yet Jesus reminds us that spiritual vitality is about connection before action. A branch never strains or stresses to produce grapes; it simply absorbs nutrients from the root system. When we abide in Christ through Scripture meditation and prayerful dependence, patience, joy, and peace naturally flourish in our lives.",
            guidedPrayer = "Heavenly Father, I confess that I often try to solve problems in my own strength. Today, I choose to abide in You. Fill me with Your Holy Spirit. Let Your love flow through me into everyone I interact with today. In Jesus' name, Amen.",
            discussionQuestion = "In what area of your life are you currently striving in your own energy rather than resting in Christ?",
            readingTimeMinutes = 3
        ),
        Devotional(
            id = "dev_2",
            date = "Yesterday • August 27",
            title = "Walking in Holy Boldness",
            authorPastor = "Pastor Sarah Jenkins",
            scriptureRef = "Proverbs 28:1",
            scriptureText = "The wicked flee though no one pursues, but the righteous are as bold as a lion.",
            reflectionText = "Righteous boldness does not mean arrogance or aggressive words. It is the quiet confidence that comes from a clear conscience before God. When our sins are washed by Christ and our motives are purified, we no longer need to live in fear of what others think.",
            guidedPrayer = "Lord, grant me courage to stand for truth with gentle grace today. Remove any fear of people from my heart and replace it with reverence for You alone. Amen.",
            discussionQuestion = "Where is God calling you to step forward in faith instead of retreating in hesitation?",
            readingTimeMinutes = 4
        ),
        Devotional(
            id = "dev_3",
            date = "August 26",
            title = "A Peace That Surpasses Understanding",
            authorPastor = "Pastor Elena Vance",
            scriptureRef = "Philippians 4:6-7",
            scriptureText = "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God. And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.",
            reflectionText = "Notice the divine exchange in Philippians 4: we give God our anxious burdens and sincere thanksgiving, and He gives us His transcendent peace. God's peace does not mean the absence of storm; it means a fortress around our mind and emotions in the midst of it.",
            guidedPrayer = "Lord, here are the worries weighing on my chest today [name them]. I trade them for Your supernatural peace. Guard my mind and heart in Christ Jesus. Amen.",
            discussionQuestion = "What specific worry can you hand over in prayer right now with genuine thanksgiving?",
            readingTimeMinutes = 3
        ),
        Devotional(
            id = "dev_4",
            date = "August 25",
            title = "Renewed Strength for the Weary",
            authorPastor = "Pastor Marcus Hayes",
            scriptureRef = "Isaiah 40:29-31",
            scriptureText = "He gives strength to the weary and increases the power of the weak. Even youths grow tired and weary, and young men stumble and fall; but those who hope in the Lord will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint.",
            reflectionText = "Human stamina has finite limits, but God's reservoir of grace never runs dry. Waiting on the Lord is an active posture of expectant trust, recognizing that His timing and power are perfect.",
            guidedPrayer = "Father, when my energy fails, remind me that Your grace is sufficient. Lift my spirit above earthly discouragements and renew my passion to serve You. Amen.",
            discussionQuestion = "How do you practice waiting on the Lord when feeling physically or emotionally drained?",
            readingTimeMinutes = 4
        )
    )

    val dailyVerse = DailyVerse(
        reference = "Romans 8:38-39",
        text = "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers, neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord.",
        theme = "Eternal Security & God's Love",
        date = "August 28"
    )

    val prayerGroups = listOf(
        PrayerGroup(
            id = "pg_north",
            name = "Northside Fellowship & Intercession",
            area = "North District",
            meetingDayTime = "Tuesdays @ 7:00 PM",
            locationName = "North Community Center / Room 204",
            address = "4500 North Oak Ave, Metro Area",
            leaderName = "Elder Thomas & Ruth Baker",
            leaderContact = "tbaker.fellowship@gmail.com",
            groupType = "All Welcome",
            description = "A warm neighborhood gathering focusing on verse-by-verse scripture study, personal fellowship, and praying over family and community needs.",
            memberCount = 18
        ),
        PrayerGroup(
            id = "pg_downtown",
            name = "Downtown Young Professionals & Students",
            area = "Downtown / Central",
            meetingDayTime = "Thursdays @ 6:45 PM",
            locationName = "Sanctuary Loft Coffee House",
            address = "120 Central Market St, Suite 300",
            leaderName = "Jonathan & Chloe Miller",
            leaderContact = "jonathan.prayergroup@gracechurch.org",
            groupType = "Young Adults",
            description = "Connecting young workers, graduates, and students navigating career and faith. Includes dinner, worship, and honest small-group discussions.",
            memberCount = 24
        ),
        PrayerGroup(
            id = "pg_westside",
            name = "Westside Family Grace Circle",
            area = "Westside",
            meetingDayTime = "Wednesdays @ 6:30 PM",
            locationName = "Westside Chapel Annex",
            address = "7820 Westview Blvd",
            leaderName = "Deacon Marcus Campbell",
            leaderContact = "campbell.westprayer@gmail.com",
            groupType = "Families & Couples",
            description = "Childcare provided! Parents and couples come together to pray for schools, marriages, and children while growing in biblical parenting.",
            memberCount = 16
        ),
        PrayerGroup(
            id = "pg_east",
            name = "East Valley Morning Dawn Watchers",
            area = "East Valley",
            meetingDayTime = "Saturdays @ 7:30 AM",
            locationName = "Grace East Pavilion",
            address = "3100 Sunrise Way",
            leaderName = "Pastor Elena Vance & Sister Clara",
            leaderContact = "elena.vance@gracechurch.org",
            groupType = "Intercessory Prayer",
            description = "An early morning dedicated intercession hour praying for world missions, church leadership, local hospitals, and regional revival.",
            memberCount = 14
        ),
        PrayerGroup(
            id = "pg_south",
            name = "South Hills Men of Valor Brotherhood",
            area = "South Hills",
            meetingDayTime = "Mondays @ 6:30 AM",
            locationName = "South Hills Fellowship Hall",
            address = "520 Ridge Point Dr",
            leaderName = "Robert Langford",
            leaderContact = "robert.langford@menofgrace.org",
            groupType = "Men's Fellowship",
            description = "Accountability, coffee, breakfast, and targeted prayer for men to lead with integrity at home, work, and community.",
            memberCount = 20
        ),
        PrayerGroup(
            id = "pg_womens",
            name = "Women of Grace: Sisters in the Word",
            area = "Downtown / Central",
            meetingDayTime = "Wednesdays @ 10:00 AM",
            locationName = "Grace Church Parlor Room",
            address = "700 Sanctuary Blvd",
            leaderName = "Pastor Sarah Jenkins",
            leaderContact = "sarah.jenkins@gracechurch.org",
            groupType = "Women's Grace",
            description = "A refreshing mid-morning oasis for women of all ages. Deep Bible study, mutual encouragement, tea, and heartfelt prayer circles.",
            memberCount = 22
        )
    )

    val allBibleBooksMetadata = listOf(
        // Old Testament (39)
        BibleBook("genesis", "Genesis", "Old Testament", "Law / Pentateuch", 50, listOf(
            BibleChapter("Genesis", 1, listOf(
                BibleVerse("Genesis", 1, 1, "In the beginning God created the heavens and the earth."),
                BibleVerse("Genesis", 1, 2, "Now the earth was formless and empty, darkness was over the surface of the deep, and the Spirit of God was hovering over the waters."),
                BibleVerse("Genesis", 1, 3, "And God said, 'Let there be light,' and there was light."),
                BibleVerse("Genesis", 1, 27, "So God created mankind in his own image, in the image of God he created them; male and female he created them."),
                BibleVerse("Genesis", 1, 31, "God saw all that he had made, and it was very good. And there was evening, and there was morning—the sixth day.")
            ))
        )),
        BibleBook("exodus", "Exodus", "Old Testament", "Law / Pentateuch", 40, listOf(
            BibleChapter("Exodus", 20, listOf(
                BibleVerse("Exodus", 20, 1, "And God spoke all these words:"),
                BibleVerse("Exodus", 20, 2, "I am the Lord your God, who brought you out of Egypt, out of the land of slavery."),
                BibleVerse("Exodus", 20, 3, "You shall have no other gods before me.")
            ))
        )),
        BibleBook("leviticus", "Leviticus", "Old Testament", "Law / Pentateuch", 27),
        BibleBook("numbers", "Numbers", "Old Testament", "Law / Pentateuch", 36),
        BibleBook("deuteronomy", "Deuteronomy", "Old Testament", "Law / Pentateuch", 34, listOf(
            BibleChapter("Deuteronomy", 6, listOf(
                BibleVerse("Deuteronomy", 6, 4, "Hear, O Israel: The Lord our God, the Lord is one."),
                BibleVerse("Deuteronomy", 6, 5, "Love the Lord your God with all your heart and with all your soul and with all your strength.")
            ))
        )),
        BibleBook("joshua", "Joshua", "Old Testament", "Historical Books", 24, listOf(
            BibleChapter("Joshua", 1, listOf(
                BibleVerse("Joshua", 1, 9, "Have I not commanded you? Be strong and courageous. Do not be afraid; do not be discouraged, for the Lord your God will be with you wherever you go.")
            ))
        )),
        BibleBook("judges", "Judges", "Old Testament", "Historical Books", 21),
        BibleBook("ruth", "Ruth", "Old Testament", "Historical Books", 4),
        BibleBook("1_samuel", "1 Samuel", "Old Testament", "Historical Books", 31),
        BibleBook("2_samuel", "2 Samuel", "Old Testament", "Historical Books", 24),
        BibleBook("1_kings", "1 Kings", "Old Testament", "Historical Books", 22),
        BibleBook("2_kings", "2 Kings", "Old Testament", "Historical Books", 25),
        BibleBook("1_chronicles", "1 Chronicles", "Old Testament", "Historical Books", 29),
        BibleBook("2_chronicles", "2 Chronicles", "Old Testament", "Historical Books", 36, listOf(
            BibleChapter("2 Chronicles", 7, listOf(
                BibleVerse("2 Chronicles", 7, 14, "If my people, who are called by my name, will humble themselves and pray and seek my face and turn from their wicked ways, then I will hear from heaven, and I will forgive their sin and will heal their land.")
            ))
        )),
        BibleBook("ezra", "Ezra", "Old Testament", "Historical Books", 10),
        BibleBook("nehemiah", "Nehemiah", "Old Testament", "Historical Books", 13),
        BibleBook("esther", "Esther", "Old Testament", "Historical Books", 10),
        BibleBook("job", "Job", "Old Testament", "Wisdom & Poetry", 42),
        BibleBook("psalms", "Psalms", "Old Testament", "Wisdom & Poetry", 150, listOf(
            BibleChapter("Psalms", 23, listOf(
                BibleVerse("Psalms", 23, 1, "The Lord is my shepherd, I lack nothing."),
                BibleVerse("Psalms", 23, 2, "He makes me lie down in green pastures, he leads me beside quiet waters,"),
                BibleVerse("Psalms", 23, 3, "he refreshes my soul. He guides me along the right paths for his name’s sake."),
                BibleVerse("Psalms", 23, 4, "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me."),
                BibleVerse("Psalms", 23, 5, "You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows."),
                BibleVerse("Psalms", 23, 6, "Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the Lord forever.")
            )),
            BibleChapter("Psalms", 46, listOf(
                BibleVerse("Psalms", 46, 1, "God is our refuge and strength, an ever-present help in trouble."),
                BibleVerse("Psalms", 46, 2, "Therefore we will not fear, though the earth give way and the mountains fall into the heart of the sea,"),
                BibleVerse("Psalms", 46, 10, "He says, 'Be still, and know that I am God; I will be exalted among the nations, I will be exalted in the earth.'"),
                BibleVerse("Psalms", 46, 11, "The Lord Almighty is with us; the God of Jacob is our fortress.")
            )),
            BibleChapter("Psalms", 91, listOf(
                BibleVerse("Psalms", 91, 1, "Whoever dwells in the shelter of the Most High will rest in the shadow of the Almighty."),
                BibleVerse("Psalms", 91, 2, "I will say of the Lord, 'He is my refuge and my fortress, my God, in whom I trust.'"),
                BibleVerse("Psalms", 91, 4, "He will cover you with his feathers, and under his wings you will find refuge; his faithfulness will be your shield and rampart.")
            )),
            BibleChapter("Psalms", 121, listOf(
                BibleVerse("Psalms", 121, 1, "I lift up my eyes to the mountains—where does my help come from?"),
                BibleVerse("Psalms", 121, 2, "My help comes from the Lord, the Maker of heaven and earth."),
                BibleVerse("Psalms", 121, 7, "The Lord will keep you from all harm—he will watch over your life;"),
                BibleVerse("Psalms", 121, 8, "the Lord will watch over your coming and going both now and forevermore.")
            ))
        )),
        BibleBook("proverbs", "Proverbs", "Old Testament", "Wisdom & Poetry", 31, listOf(
            BibleChapter("Proverbs", 3, listOf(
                BibleVerse("Proverbs", 3, 5, "Trust in the Lord with all your heart and lean not on your own understanding;"),
                BibleVerse("Proverbs", 3, 6, "in all your ways submit to him, and he will make your paths straight."),
                BibleVerse("Proverbs", 3, 7, "Do not be wise in your own eyes; fear the Lord and shun evil."),
                BibleVerse("Proverbs", 3, 8, "This will bring health to your body and nourishment to your bones.")
            )),
            BibleChapter("Proverbs", 28, listOf(
                BibleVerse("Proverbs", 28, 1, "The wicked flee though no one pursues, but the righteous are as bold as a lion.")
            ))
        )),
        BibleBook("ecclesiastes", "Ecclesiastes", "Old Testament", "Wisdom & Poetry", 12, listOf(
            BibleChapter("Ecclesiastes", 3, listOf(
                BibleVerse("Ecclesiastes", 3, 1, "There is a time for everything, and a season for every activity under the heavens."),
                BibleVerse("Ecclesiastes", 3, 11, "He has made everything beautiful in its time. He has also set eternity in the human heart.")
            ))
        )),
        BibleBook("song_of_solomon", "Song of Solomon", "Old Testament", "Wisdom & Poetry", 8),
        BibleBook("isaiah", "Isaiah", "Old Testament", "Major Prophets", 66, listOf(
            BibleChapter("Isaiah", 40, listOf(
                BibleVerse("Isaiah", 40, 29, "He gives strength to the weary and increases the power of the weak."),
                BibleVerse("Isaiah", 40, 31, "but those who hope in the Lord will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint.")
            )),
            BibleChapter("Isaiah", 53, listOf(
                BibleVerse("Isaiah", 53, 5, "But he was pierced for our transgressions, he was crushed for our iniquities; the punishment that brought us peace was on him, and by his wounds we are healed.")
            ))
        )),
        BibleBook("jeremiah", "Jeremiah", "Old Testament", "Major Prophets", 52, listOf(
            BibleChapter("Jeremiah", 29, listOf(
                BibleVerse("Jeremiah", 29, 11, "'For I know the plans I have for you,' declares the Lord, 'plans to prosper you and not to harm you, plans to give you hope and a future.'")
            ))
        )),
        BibleBook("lamentations", "Lamentations", "Old Testament", "Major Prophets", 5, listOf(
            BibleChapter("Lamentations", 3, listOf(
                BibleVerse("Lamentations", 3, 22, "Because of the Lord’s great love we are not consumed, for his compassions never fail."),
                BibleVerse("Lamentations", 3, 23, "They are new every morning; great is your faithfulness.")
            ))
        )),
        BibleBook("ezekiel", "Ezekiel", "Old Testament", "Major Prophets", 48),
        BibleBook("daniel", "Daniel", "Old Testament", "Major Prophets", 12),
        BibleBook("hosea", "Hosea", "Old Testament", "Minor Prophets", 14),
        BibleBook("joel", "Joel", "Old Testament", "Minor Prophets", 3),
        BibleBook("amos", "Amos", "Old Testament", "Minor Prophets", 9),
        BibleBook("obadiah", "Obadiah", "Old Testament", "Minor Prophets", 1),
        BibleBook("jonah", "Jonah", "Old Testament", "Minor Prophets", 4),
        BibleBook("micah", "Micah", "Old Testament", "Minor Prophets", 7, listOf(
            BibleChapter("Micah", 6, listOf(
                BibleVerse("Micah", 6, 8, "He has shown you, O mortal, what is good. And what does the Lord require of you? To act justly and to love mercy and to walk humbly with your God.")
            ))
        )),
        BibleBook("nahum", "Nahum", "Old Testament", "Minor Prophets", 3),
        BibleBook("habakkuk", "Habakkuk", "Old Testament", "Minor Prophets", 3),
        BibleBook("zephaniah", "Zephaniah", "Old Testament", "Minor Prophets", 3),
        BibleBook("haggai", "Haggai", "Old Testament", "Minor Prophets", 2),
        BibleBook("zechariah", "Zechariah", "Old Testament", "Minor Prophets", 14),
        BibleBook("malachi", "Malachi", "Old Testament", "Minor Prophets", 4),

        // New Testament (27)
        BibleBook("matthew", "Matthew", "New Testament", "Gospels", 28, listOf(
            BibleChapter("Matthew", 5, listOf(
                BibleVerse("Matthew", 5, 1, "Now when Jesus saw the crowds, he went up on a mountainside and sat down. His disciples came to him,"),
                BibleVerse("Matthew", 5, 2, "and he began to teach them."),
                BibleVerse("Matthew", 5, 3, "Blessed are the poor in spirit, for theirs is the kingdom of heaven."),
                BibleVerse("Matthew", 5, 4, "Blessed are those who mourn, for they will be comforted."),
                BibleVerse("Matthew", 5, 5, "Blessed are the meek, for they will inherit the earth."),
                BibleVerse("Matthew", 5, 6, "Blessed are those who hunger and thirst for righteousness, for they will be filled."),
                BibleVerse("Matthew", 5, 7, "Blessed are the merciful, for they will be shown mercy."),
                BibleVerse("Matthew", 5, 8, "Blessed are the pure in heart, for they will see God."),
                BibleVerse("Matthew", 5, 9, "Blessed are the peacemakers, for they will be called children of God."),
                BibleVerse("Matthew", 5, 14, "You are the light of the world. A town built on a hill cannot be hidden."),
                BibleVerse("Matthew", 5, 16, "In the same way, let your light shine before others, that they may see your good deeds and glorify your Father in heaven.")
            )),
            BibleChapter("Matthew", 6, listOf(
                BibleVerse("Matthew", 6, 9, "This, then, is how you should pray: 'Our Father in heaven, hallowed be your name,"),
                BibleVerse("Matthew", 6, 10, "your kingdom come, your will be done, on earth as it is in heaven."),
                BibleVerse("Matthew", 6, 11, "Give us today our daily bread."),
                BibleVerse("Matthew", 6, 12, "And forgive us our debts, as we also have forgiven our debtors."),
                BibleVerse("Matthew", 6, 13, "And lead us not into temptation, but deliver us from the evil one.'"),
                BibleVerse("Matthew", 6, 33, "But seek first his kingdom and his righteousness, and all these things will be given to you as well."),
                BibleVerse("Matthew", 6, 34, "Therefore do not worry about tomorrow, for tomorrow will worry about itself. Each day has enough trouble of its own.")
            )),
            BibleChapter("Matthew", 28, listOf(
                BibleVerse("Matthew", 28, 18, "Then Jesus came to them and said, 'All authority in heaven and on earth has been given to me.'"),
                BibleVerse("Matthew", 28, 19, "Therefore go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit,"),
                BibleVerse("Matthew", 28, 20, "and teaching them to obey everything I have commanded you. And surely I am with you always, to the very end of the age.")
            ))
        )),
        BibleBook("mark", "Mark", "New Testament", "Gospels", 16, listOf(
            BibleChapter("Mark", 10, listOf(
                BibleVerse("Mark", 10, 45, "For even the Son of Man did not come to be served, but to serve, and to give his life as a ransom for many.")
            ))
        )),
        BibleBook("luke", "Luke", "New Testament", "Gospels", 24, listOf(
            BibleChapter("Luke", 2, listOf(
                BibleVerse("Luke", 2, 10, "But the angel said to them, 'Do not be afraid. I bring you good news that will cause great joy for all the people.'"),
                BibleVerse("Luke", 2, 11, "Today in the town of David a Savior has been born to you; he is the Messiah, the Lord.")
            ))
        )),
        BibleBook("john", "John", "New Testament", "Gospels", 21, listOf(
            BibleChapter("John", 1, listOf(
                BibleVerse("John", 1, 1, "In the beginning was the Word, and the Word was with God, and the Word was God."),
                BibleVerse("John", 1, 2, "He was with God in the beginning."),
                BibleVerse("John", 1, 3, "Through him all things were made; without him nothing was made that has been made."),
                BibleVerse("John", 1, 4, "In him was life, and that life was the light of all mankind."),
                BibleVerse("John", 1, 5, "The light shines in the darkness, and the darkness has not overcome it."),
                BibleVerse("John", 1, 14, "The Word became flesh and made his dwelling among us. We have seen his glory, the glory of the one and only Son, who came from the Father, full of grace and truth.")
            )),
            BibleChapter("John", 3, listOf(
                BibleVerse("John", 3, 16, "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life."),
                BibleVerse("John", 3, 17, "For God did not send his Son into the world to condemn the world, but to save the world through him.")
            )),
            BibleChapter("John", 14, listOf(
                BibleVerse("John", 14, 1, "Do not let your hearts be troubled. You believe in God; believe also in me."),
                BibleVerse("John", 14, 2, "My Father’s house has many rooms; if that were not so, would I have told you that I am going there to prepare a place for you?"),
                BibleVerse("John", 14, 6, "Jesus answered, 'I am the way and the truth and the life. No one comes to the Father except through me.'"),
                BibleVerse("John", 14, 27, "Peace I leave with you; my peace I give you. I do not give to you as the world gives. Do not let your hearts be troubled and do not be afraid.")
            )),
            BibleChapter("John", 15, listOf(
                BibleVerse("John", 15, 4, "Remain in me, as I also remain in you. No branch can bear fruit by itself; it must remain in the vine. Neither can you bear fruit unless you remain in me."),
                BibleVerse("John", 15, 5, "I am the vine; you are the branches. If you remain in me and I in you, you will bear much fruit; apart from me you can do nothing."),
                BibleVerse("John", 15, 9, "As the Father has loved me, so have I loved you. Now remain in my love.")
            ))
        )),
        BibleBook("acts", "Acts", "New Testament", "History", 28, listOf(
            BibleChapter("Acts", 1, listOf(
                BibleVerse("Acts", 1, 8, "But you will receive power when the Holy Spirit comes on you; and you will be my witnesses in Jerusalem, and in all Judea and Samaria, and to the ends of the earth.")
            )),
            BibleChapter("Acts", 2, listOf(
                BibleVerse("Acts", 2, 42, "They devoted themselves to the apostles’ teaching and to fellowship, to the breaking of bread and to prayer.")
            ))
        )),
        BibleBook("romans", "Romans", "New Testament", "Epistles", 16, listOf(
            BibleChapter("Romans", 8, listOf(
                BibleVerse("Romans", 8, 1, "Therefore, there is now no condemnation for those who are in Christ Jesus,"),
                BibleVerse("Romans", 8, 28, "And we know that in all things God works for the good of those who love him, who have been called according to his purpose."),
                BibleVerse("Romans", 8, 31, "What, then, shall we say in response to these things? If God is for us, who can be against us?"),
                BibleVerse("Romans", 8, 38, "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers,"),
                BibleVerse("Romans", 8, 39, "neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord.")
            )),
            BibleChapter("Romans", 12, listOf(
                BibleVerse("Romans", 12, 1, "Therefore, I urge you, brothers and sisters, in view of God’s mercy, to offer your bodies as a living sacrifice, holy and pleasing to God—this is your true and proper worship."),
                BibleVerse("Romans", 12, 2, "Do not conform to the pattern of this world, but be transformed by the renewing of your mind. Then you will be able to test and approve what God’s will is—his good, pleasing and perfect will."),
                BibleVerse("Romans", 12, 12, "Be joyful in hope, patient in affliction, faithful in prayer.")
            ))
        )),
        BibleBook("1_corinthians", "1 Corinthians", "New Testament", "Epistles", 16, listOf(
            BibleChapter("1 Corinthians", 13, listOf(
                BibleVerse("1 Corinthians", 13, 4, "Love is patient, love is kind. It does not envy, it does not boast, it is not proud."),
                BibleVerse("1 Corinthians", 13, 7, "It always protects, always trusts, always hopes, always perseveres."),
                BibleVerse("1 Corinthians", 13, 13, "And now these three remain: faith, hope and love. But the greatest of these is love.")
            ))
        )),
        BibleBook("2_corinthians", "2 Corinthians", "New Testament", "Epistles", 13, listOf(
            BibleChapter("2 Corinthians", 5, listOf(
                BibleVerse("2 Corinthians", 5, 17, "Therefore, if anyone is in Christ, the new creation has come: The old has gone, the new is here!"),
                BibleVerse("2 Corinthians", 5, 20, "We are therefore Christ’s ambassadors, as though God were making his appeal through us.")
            ))
        )),
        BibleBook("galatians", "Galatians", "New Testament", "Epistles", 6, listOf(
            BibleChapter("Galatians", 5, listOf(
                BibleVerse("Galatians", 5, 22, "But the fruit of the Spirit is love, joy, peace, forbearance, kindness, goodness, faithfulness,"),
                BibleVerse("Galatians", 5, 23, "gentleness and self-control. Against such things there is no law.")
            ))
        )),
        BibleBook("ephesians", "Ephesians", "New Testament", "Epistles", 6, listOf(
            BibleChapter("Ephesians", 2, listOf(
                BibleVerse("Ephesians", 2, 8, "For it is by grace you have been saved, through faith—and this is not from yourselves, it is the gift of God—"),
                BibleVerse("Ephesians", 2, 9, "not by works, so that no one can boast."),
                BibleVerse("Ephesians", 2, 10, "For we are God’s handiwork, created in Christ Jesus to do good works, which God prepared in advance for us to do.")
            )),
            BibleChapter("Ephesians", 6, listOf(
                BibleVerse("Ephesians", 6, 10, "Finally, be strong in the Lord and in his mighty power."),
                BibleVerse("Ephesians", 6, 11, "Put on the full armor of God, so that you can take your stand against the devil’s schemes.")
            ))
        )),
        BibleBook("philippians", "Philippians", "New Testament", "Epistles", 4, listOf(
            BibleChapter("Philippians", 4, listOf(
                BibleVerse("Philippians", 4, 4, "Rejoice in the Lord always. I will say it again: Rejoice!"),
                BibleVerse("Philippians", 4, 6, "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God."),
                BibleVerse("Philippians", 4, 7, "And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus."),
                BibleVerse("Philippians", 4, 13, "I can do all this through him who gives me strength."),
                BibleVerse("Philippians", 4, 19, "And my God will meet all your needs according to the riches of his glory in Christ Jesus.")
            ))
        )),
        BibleBook("colossians", "Colossians", "New Testament", "Epistles", 4, listOf(
            BibleChapter("Colossians", 3, listOf(
                BibleVerse("Colossians", 3, 12, "Therefore, as God’s chosen people, holy and dearly loved, clothe yourselves with compassion, kindness, humility, gentleness and patience.")
            ))
        )),
        BibleBook("1_thessalonians", "1 Thessalonians", "New Testament", "Epistles", 5, listOf(
            BibleChapter("1 Thessalonians", 5, listOf(
                BibleVerse("1 Thessalonians", 5, 16, "Rejoice always,"),
                BibleVerse("1 Thessalonians", 5, 17, "pray continually,"),
                BibleVerse("1 Thessalonians", 5, 18, "give thanks in all circumstances; for this is God’s will for you in Christ Jesus.")
            ))
        )),
        BibleBook("2_thessalonians", "2 Thessalonians", "New Testament", "Epistles", 3),
        BibleBook("1_timothy", "1 Timothy", "New Testament", "Epistles", 6),
        BibleBook("2_timothy", "2 Timothy", "New Testament", "Epistles", 4, listOf(
            BibleChapter("2 Timothy", 1, listOf(
                BibleVerse("2 Timothy", 1, 7, "For the Spirit God gave us does not make us timid, but gives us power, love and self-discipline.")
            ))
        )),
        BibleBook("titus", "Titus", "New Testament", "Epistles", 3),
        BibleBook("philemon", "Philemon", "New Testament", "Epistles", 1),
        BibleBook("hebrews", "Hebrews", "New Testament", "General Epistles", 13, listOf(
            BibleChapter("Hebrews", 11, listOf(
                BibleVerse("Hebrews", 11, 1, "Now faith is confidence in what we hope for and assurance about what we do not see."),
                BibleVerse("Hebrews", 11, 6, "And without faith it is impossible to please God, because anyone who comes to him must believe that he exists and that he rewards those who earnestly seek him.")
            )),
            BibleChapter("Hebrews", 12, listOf(
                BibleVerse("Hebrews", 12, 1, "Therefore, since we are surrounded by such a great cloud of witnesses, let us throw off everything that hinders and the sin that so easily entangles. And let us run with perseverance the race marked out for us,"),
                BibleVerse("Hebrews", 12, 2, "fixing our eyes on Jesus, the pioneer and perfecter of faith.")
            ))
        )),
        BibleBook("james", "James", "New Testament", "General Epistles", 5, listOf(
            BibleChapter("James", 1, listOf(
                BibleVerse("James", 1, 2, "Consider it pure joy, my brothers and sisters, whenever you face trials of many kinds,"),
                BibleVerse("James", 1, 5, "If any of you lacks wisdom, you should ask God, who gives generously to all without finding fault, and it will be given to you.")
            ))
        )),
        BibleBook("1_peter", "1 Peter", "New Testament", "General Epistles", 5, listOf(
            BibleChapter("1 Peter", 5, listOf(
                BibleVerse("1 Peter", 5, 7, "Cast all your anxiety on him because he cares for you.")
            ))
        )),
        BibleBook("2_peter", "2 Peter", "New Testament", "General Epistles", 3),
        BibleBook("1_john", "1 John", "New Testament", "General Epistles", 5, listOf(
            BibleChapter("1 John", 4, listOf(
                BibleVerse("1 John", 4, 18, "There is no fear in love. But perfect love drives out fear, because fear has to do with punishment. The one who fears is not made perfect in love."),
                BibleVerse("1 John", 4, 19, "We love because he first loved us.")
            ))
        )),
        BibleBook("2_john", "2 John", "New Testament", "General Epistles", 1),
        BibleBook("3_john", "3 John", "New Testament", "General Epistles", 1),
        BibleBook("jude", "Jude", "New Testament", "General Epistles", 1),
        BibleBook("revelation", "Revelation", "New Testament", "Prophecy", 22, listOf(
            BibleChapter("Revelation", 21, listOf(
                BibleVerse("Revelation", 21, 3, "And I heard a loud voice from the throne saying, 'Look! God’s dwelling place is now among the people, and he will dwell with them. They will be his people, and God himself will be with them and be their God.'"),
                BibleVerse("Revelation", 21, 4, "'He will wipe every tear from their eyes. There will be no more death or mourning or crying or pain, for the old order of things has passed away.'")
            )),
            BibleChapter("Revelation", 22, listOf(
                BibleVerse("Revelation", 22, 13, "I am the Alpha and the Omega, the First and the Last, the Beginning and the End."),
                BibleVerse("Revelation", 22, 20, "He who testifies to these things says, 'Yes, I am coming soon.' Amen. Come, Lord Jesus.")
            ))
        ))
    )

    val bibleBooks: List<BibleBook> = allBibleBooksMetadata

    fun findBookByName(query: String): BibleBook? {
        val clean = query.trim().lowercase()
        return allBibleBooksMetadata.find { it.name.equals(clean, ignoreCase = true) }
            ?: allBibleBooksMetadata.find { it.name.lowercase().startsWith(clean) }
            ?: allBibleBooksMetadata.find { it.name.lowercase().contains(clean) }
    }

    fun getChapterForBook(book: BibleBook, chapterNum: Int): BibleChapter {
        val existing = book.chapters.find { it.chapterNumber == chapterNum }
        if (existing != null && existing.verses.isNotEmpty()) {
            return existing
        }
        // Fallback generator for chapters without explicit seed text
        val safeChapter = chapterNum.coerceIn(1, book.chapterCount.coerceAtLeast(1))
        val sampleVerses = listOf(
            BibleVerse(book.name, safeChapter, 1, "The word of the Lord came to the congregation, declaring grace, peace, and steadfast love forever."),
            BibleVerse(book.name, safeChapter, 2, "Seek the Lord while he may be found; call on him while he is near. For his mercy endures to all generations."),
            BibleVerse(book.name, safeChapter, 3, "Blessed is the one who trusts in the Lord, whose confidence is in Him. They will be like a tree planted by the water."),
            BibleVerse(book.name, safeChapter, 4, "Give thanks to the Lord, for he is good; his faithful love endures forever and ever."),
            BibleVerse(book.name, safeChapter, 5, "May the peace of Christ rule in your hearts, since as members of one body you were called to peace.")
        )
        return BibleChapter(book.name, safeChapter, sampleVerses)
    }

    val initialPrayerRequests: List<PrayerRequestEntity> = listOf(
        PrayerRequestEntity(
            id = 1,
            authorName = "Hannah Miller",
            isAnonymous = false,
            area = "North District",
            title = "Healing for my mother following surgery",
            details = "Please keep my mom in your prayers as she recovers from hip replacement surgery this week. Praying for smooth rehabilitation and pain relief.",
            prayerCount = 19,
            timestamp = System.currentTimeMillis() - 3600000 * 5
        ),
        PrayerRequestEntity(
            id = 2,
            authorName = "Anonymous Member",
            isAnonymous = true,
            area = "Downtown / Central",
            title = "Guidance in major career decision",
            details = "Praying for God's wisdom and clear direction regarding a job relocation opportunity that impacts our entire family.",
            prayerCount = 14,
            timestamp = System.currentTimeMillis() - 3600000 * 12
        ),
        PrayerRequestEntity(
            id = 3,
            authorName = "Samuel & Grace Ortiz",
            isAnonymous = false,
            area = "Westside",
            title = "Thanksgiving for new baby daughter!",
            details = "We welcomed baby Evelyn into the world yesterday healthy and strong! Thank you church family for all your love and prayers!",
            prayerCount = 42,
            isAnswered = true,
            timestamp = System.currentTimeMillis() - 3600000 * 24
        ),
        PrayerRequestEntity(
            id = 4,
            authorName = "Caleb Wright",
            isAnonymous = false,
            area = "East Valley",
            title = "Campus Outreach Revival",
            details = "Pray for our university Christian fellowship as we prepare for welcome week. May many students find hope and community in Christ.",
            prayerCount = 27,
            timestamp = System.currentTimeMillis() - 3600000 * 36
        )
    )

    val staffUsers = listOf(
        CompanionStaffUser(
            id = "staff_david",
            name = "Dr. David Sterling",
            email = "pastor.david@gracechurch.org",
            role = "Senior Pastor",
            title = "Senior Pastor & Head of Ministry",
            avatarInitials = "DS",
            accessLevel = "Full Pastoral Authority",
            defaultCategory = "Pastoral Letter"
        ),
        CompanionStaffUser(
            id = "staff_sarah",
            name = "Pastor Sarah Jenkins",
            email = "sarah.jenkins@gracechurch.org",
            role = "Pastoral Care Director",
            title = "Executive & Pastoral Care Pastor",
            avatarInitials = "SJ",
            accessLevel = "Pastoral & Counseling Admin",
            defaultCategory = "Prayer Bulletin"
        ),
        CompanionStaffUser(
            id = "staff_marcus",
            name = "Pastor Marcus Hayes",
            email = "marcus.hayes@gracechurch.org",
            role = "Youth & Young Adults Pastor",
            title = "Next Gen Ministry Leader",
            avatarInitials = "MH",
            accessLevel = "Ministry Publishing & Alerts",
            defaultCategory = "Event & Gathering"
        ),
        CompanionStaffUser(
            id = "staff_admin",
            name = "Media & Communications Office",
            email = "admin@gracechurch.org",
            role = "Communications Admin",
            title = "Media Director & Church Administrator",
            avatarInitials = "GC",
            accessLevel = "Master Broadcast & Scheduling",
            defaultCategory = "Urgent Announcement"
        )
    )

    val initialAnnouncements = listOf(
        AnnouncementEntity(
            id = 1,
            title = "Pastoral Letter: Walking in Steadfast Hope",
            content = "Dear Grace Church family, as we step into this new season, my heart is deeply stirred by Paul's exhortation in Romans 15:13. Whatever burdens or uncertainties you carry this week, know that Christ remains our unshakeable anchor. Our pastoral team is praying over every household daily.",
            authorPastorName = "Dr. David Sterling",
            authorRole = "Senior Pastor",
            category = "Pastoral Letter",
            scriptureRef = "Romans 15:13",
            actionButtonText = "Read Scripture Focus",
            actionButtonLink = "scripture:Romans:15",
            isPinned = true,
            isScheduled = false,
            scheduledTimestamp = 0L,
            scheduledDateFormatted = "Published Live",
            status = "Published",
            sendPushNotification = true,
            notificationSent = true,
            notificationTitle = "Pastoral Letter from Dr. David Sterling",
            notificationBody = "\"May the God of hope fill you with all joy and peace as you trust in Him.\"",
            priorityLevel = "Urgent",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2 // 2 hours ago
        ),
        AnnouncementEntity(
            id = 2,
            title = "Sunday Sanctuary Worship & Holy Communion",
            content = "Join us this Sunday across all sanctuary services (8:30 AM & 10:30 AM) as we celebrate Holy Communion and welcome guest speaker Dr. Arthur Vance for our 'Anchored in Truth' series. Childcare and Youth Fellowship are available during both services.",
            authorPastorName = "Pastor Sarah Jenkins",
            authorRole = "Pastoral Care Director",
            category = "Event & Gathering",
            scriptureRef = "1 Corinthians 11:24-26",
            actionButtonText = "View Sunday Guide",
            actionButtonLink = "worship_guide",
            isPinned = true,
            isScheduled = false,
            scheduledTimestamp = 0L,
            scheduledDateFormatted = "Published Live",
            status = "Published",
            sendPushNotification = true,
            notificationSent = true,
            notificationTitle = "Sunday Worship & Holy Communion",
            notificationBody = "Join us this Sunday at 8:30 AM & 10:30 AM in the Main Sanctuary.",
            priorityLevel = "High",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 8 // 8 hours ago
        ),
        AnnouncementEntity(
            id = 3,
            title = "Midweek Community Prayer Vigil & Intercession",
            content = "We invite all members, families, and prayer teams to gather Wednesday at 7:00 PM in the Chapel of Grace for an evening of unified intercession, worship, and laying on of hands for healing and breakthroughs.",
            authorPastorName = "Pastor Elena Vance",
            authorRole = "Worship & Prayer Pastor",
            category = "Prayer Bulletin",
            scriptureRef = "2 Chronicles 7:14",
            actionButtonText = "Join Prayer Chain",
            actionButtonLink = "community_prayer",
            isPinned = false,
            isScheduled = false,
            scheduledTimestamp = 0L,
            scheduledDateFormatted = "Published Live",
            status = "Published",
            sendPushNotification = false,
            notificationSent = false,
            notificationTitle = "Wednesday Prayer Vigil",
            notificationBody = "Gather with us Wednesday at 7:00 PM for prayer and worship.",
            priorityLevel = "Standard",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 22 // 22 hours ago
        ),
        AnnouncementEntity(
            id = 4,
            title = "Youth Winter Retreat 2026 Registration",
            content = "Early bird registration is officially open for our annual Youth & Young Adult Winter Retreat at Pinecrest Summit! Featuring guest worship leaders, outdoor campfires, and powerful biblical workshops. Spots are limited, reserve your spot today.",
            authorPastorName = "Pastor Marcus Hayes",
            authorRole = "Youth Pastor",
            category = "Ministry Update",
            scriptureRef = "1 Timothy 4:12",
            actionButtonText = "View Details",
            actionButtonLink = "youth_retreat",
            isPinned = false,
            isScheduled = true,
            scheduledTimestamp = System.currentTimeMillis() + 1000 * 60 * 60 * 36, // in 36 hours
            scheduledDateFormatted = "Tomorrow at 7:00 AM",
            status = "Scheduled",
            sendPushNotification = true,
            notificationSent = false,
            notificationTitle = "Youth Winter Retreat Registration Alert",
            notificationBody = "Early registration for Pinecrest Summit is now open! Reserve your spot.",
            priorityLevel = "High",
            timestamp = System.currentTimeMillis() + 1000 * 60 * 60 * 36
        )
    )
}
