package com.example.data.repository

import com.example.data.local.PrayerRequestEntity
import com.example.data.model.*

object ChurchDataSeed {

    val pastors = listOf(
        Pastor(
            id = "pastor_david",
            name = "Dr. David Sterling",
            title = "Senior Pastor",
            bio = "Dr. David Sterling has served Grace Church for over 16 years. He holds a Ph.D. in New Testament theology from Oxford and has a heart for gospel-centered expository preaching and community transformation.",
            email = "pastor.david@gracechurch.org",
            phone = "(555) 234-8901",
            officeHours = "Tue & Thu: 9:00 AM – 3:00 PM",
            specialty = listOf("Expository Preaching", "Leadership", "Theological Counseling")
        ),
        Pastor(
            id = "pastor_sarah",
            name = "Pastor Sarah Jenkins",
            title = "Executive & Pastoral Care",
            bio = "Pastor Sarah oversees family ministry and pastoral care. With a Master of Divinity and certification in Christian counseling, she brings warmth and biblical empathy to every pastoral meeting.",
            email = "sarah.jenkins@gracechurch.org",
            phone = "(555) 234-8902",
            officeHours = "Mon, Wed & Fri: 10:00 AM – 4:00 PM",
            specialty = listOf("Grief & Recovery", "Marriage Counseling", "Women's Ministry")
        ),
        Pastor(
            id = "pastor_marcus",
            name = "Pastor Marcus Hayes",
            title = "Youth & Young Adults Pastor",
            bio = "Marcus is deeply passionate about mentoring the next generation in deep theological truth and cultural discernment. He organizes weekly collegiate prayer networks and local outreach initiatives.",
            email = "marcus.hayes@gracechurch.org",
            phone = "(555) 234-8903",
            officeHours = "Wed & Thu: 1:00 PM – 6:00 PM",
            specialty = listOf("Young Adults", "Discipleship", "Campus Outreach")
        ),
        Pastor(
            id = "pastor_elena",
            name = "Pastor Elena Vance",
            title = "Worship & Prayer Ministries Pastor",
            bio = "Elena leads Grace Church's worship teams and oversees regional intercessory prayer networks. She trains small group facilitators in biblical liturgical prayer and spiritual disciplines.",
            email = "elena.vance@gracechurch.org",
            phone = "(555) 234-8904",
            officeHours = "Tue & Fri: 11:00 AM – 4:00 PM",
            specialty = listOf("Intercessory Prayer", "Worship Arts", "Spiritual Formation")
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

    val bibleBooks = listOf(
        BibleBook(
            id = "matthew",
            name = "Matthew",
            testament = "New Testament",
            category = "Gospels",
            chapterCount = 28,
            chapters = listOf(
                BibleChapter(
                    bookName = "Matthew",
                    chapterNumber = 5,
                    verses = listOf(
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
                    )
                ),
                BibleChapter(
                    bookName = "Matthew",
                    chapterNumber = 6,
                    verses = listOf(
                        BibleVerse("Matthew", 6, 9, "This, then, is how you should pray: 'Our Father in heaven, hallowed be your name,"),
                        BibleVerse("Matthew", 6, 10, "your kingdom come, your will be done, on earth as it is in heaven."),
                        BibleVerse("Matthew", 6, 11, "Give us today our daily bread."),
                        BibleVerse("Matthew", 6, 12, "And forgive us our debts, as we also have forgiven our debtors."),
                        BibleVerse("Matthew", 6, 13, "And lead us not into temptation, but deliver us from the evil one.'"),
                        BibleVerse("Matthew", 6, 33, "But seek first his kingdom and his righteousness, and all these things will be given to you as well."),
                        BibleVerse("Matthew", 6, 34, "Therefore do not worry about tomorrow, for tomorrow will worry about itself. Each day has enough trouble of its own.")
                    )
                )
            )
        ),
        BibleBook(
            id = "john",
            name = "John",
            testament = "New Testament",
            category = "Gospels",
            chapterCount = 21,
            chapters = listOf(
                BibleChapter(
                    bookName = "John",
                    chapterNumber = 1,
                    verses = listOf(
                        BibleVerse("John", 1, 1, "In the beginning was the Word, and the Word was with God, and the Word was God."),
                        BibleVerse("John", 1, 2, "He was with God in the beginning."),
                        BibleVerse("John", 1, 3, "Through him all things were made; without him nothing was made that has been made."),
                        BibleVerse("John", 1, 4, "In him was life, and that life was the light of all mankind."),
                        BibleVerse("John", 1, 5, "The light shines in the darkness, and the darkness has not overcome it."),
                        BibleVerse("John", 1, 14, "The Word became flesh and made his dwelling among us. We have seen his glory, the glory of the one and only Son, who came from the Father, full of grace and truth.")
                    )
                ),
                BibleChapter(
                    bookName = "John",
                    chapterNumber = 14,
                    verses = listOf(
                        BibleVerse("John", 14, 1, "Do not let your hearts be troubled. You believe in God; believe also in me."),
                        BibleVerse("John", 14, 2, "My Father’s house has many rooms; if that were not so, would I have told you that I am going there to prepare a place for you?"),
                        BibleVerse("John", 14, 6, "Jesus answered, 'I am the way and the truth and the life. No one comes to the Father except through me.'"),
                        BibleVerse("John", 14, 27, "Peace I leave with you; my peace I give you. I do not give to you as the world gives. Do not let your hearts be troubled and do not be afraid.")
                    )
                ),
                BibleChapter(
                    bookName = "John",
                    chapterNumber = 15,
                    verses = listOf(
                        BibleVerse("John", 15, 4, "Remain in me, as I also remain in you. No branch can bear fruit by itself; it must remain in the vine. Neither can you bear fruit unless you remain in me."),
                        BibleVerse("John", 15, 5, "I am the vine; you are the branches. If you remain in me and I in you, you will bear much fruit; apart from me you can do nothing."),
                        BibleVerse("John", 15, 9, "As the Father has loved me, so have I loved you. Now remain in my love.")
                    )
                )
            )
        ),
        BibleBook(
            id = "psalms",
            name = "Psalms",
            testament = "Old Testament",
            category = "Wisdom & Poetry",
            chapterCount = 150,
            chapters = listOf(
                BibleChapter(
                    bookName = "Psalms",
                    chapterNumber = 23,
                    verses = listOf(
                        BibleVerse("Psalms", 23, 1, "The Lord is my shepherd, I lack nothing."),
                        BibleVerse("Psalms", 23, 2, "He makes me lie down in green pastures, he leads me beside quiet waters,"),
                        BibleVerse("Psalms", 23, 3, "he refreshes my soul. He guides me along the right paths for his name’s sake."),
                        BibleVerse("Psalms", 23, 4, "Even though I walk through the darkest valley, I will fear no evil, for you are with me; your rod and your staff, they comfort me."),
                        BibleVerse("Psalms", 23, 5, "You prepare a table before me in the presence of my enemies. You anoint my head with oil; my cup overflows."),
                        BibleVerse("Psalms", 23, 6, "Surely your goodness and love will follow me all the days of my life, and I will dwell in the house of the Lord forever.")
                    )
                ),
                BibleChapter(
                    bookName = "Psalms",
                    chapterNumber = 46,
                    verses = listOf(
                        BibleVerse("Psalms", 46, 1, "God is our refuge and strength, an ever-present help in trouble."),
                        BibleVerse("Psalms", 46, 2, "Therefore we will not fear, though the earth give way and the mountains fall into the heart of the sea,"),
                        BibleVerse("Psalms", 46, 10, "He says, 'Be still, and know that I am God; I will be exalted among the nations, I will be exalted in the earth.'"),
                        BibleVerse("Psalms", 46, 11, "The Lord Almighty is with us; the God of Jacob is our fortress.")
                    )
                ),
                BibleChapter(
                    bookName = "Psalms",
                    chapterNumber = 121,
                    verses = listOf(
                        BibleVerse("Psalms", 121, 1, "I lift up my eyes to the mountains—where does my help come from?"),
                        BibleVerse("Psalms", 121, 2, "My help comes from the Lord, the Maker of heaven and earth."),
                        BibleVerse("Psalms", 121, 7, "The Lord will keep you from all harm—he will watch over your life;"),
                        BibleVerse("Psalms", 121, 8, "the Lord will watch over your coming and going both now and forevermore.")
                    )
                )
            )
        ),
        BibleBook(
            id = "proverbs",
            name = "Proverbs",
            testament = "Old Testament",
            category = "Wisdom & Poetry",
            chapterCount = 31,
            chapters = listOf(
                BibleChapter(
                    bookName = "Proverbs",
                    chapterNumber = 3,
                    verses = listOf(
                        BibleVerse("Proverbs", 3, 5, "Trust in the Lord with all your heart and lean not on your own understanding;"),
                        BibleVerse("Proverbs", 3, 6, "in all your ways submit to him, and he will make your paths straight."),
                        BibleVerse("Proverbs", 3, 7, "Do not be wise in your own eyes; fear the Lord and shun evil."),
                        BibleVerse("Proverbs", 3, 8, "This will bring health to your body and nourishment to your bones.")
                    )
                )
            )
        ),
        BibleBook(
            id = "romans",
            name = "Romans",
            testament = "New Testament",
            category = "Epistles",
            chapterCount = 16,
            chapters = listOf(
                BibleChapter(
                    bookName = "Romans",
                    chapterNumber = 8,
                    verses = listOf(
                        BibleVerse("Romans", 8, 1, "Therefore, there is now no condemnation for those who are in Christ Jesus,"),
                        BibleVerse("Romans", 8, 28, "And we know that in all things God works for the good of those who love him, who have been called according to his purpose."),
                        BibleVerse("Romans", 8, 31, "What, then, shall we say in response to these things? If God is for us, who can be against us?"),
                        BibleVerse("Romans", 8, 38, "For I am convinced that neither death nor life, neither angels nor demons, neither the present nor the future, nor any powers,"),
                        BibleVerse("Romans", 8, 39, "neither height nor depth, nor anything else in all creation, will be able to separate us from the love of God that is in Christ Jesus our Lord.")
                    )
                ),
                BibleChapter(
                    bookName = "Romans",
                    chapterNumber = 12,
                    verses = listOf(
                        BibleVerse("Romans", 12, 1, "Therefore, I urge you, brothers and sisters, in view of God’s mercy, to offer your bodies as a living sacrifice, holy and pleasing to God—this is your true and proper worship."),
                        BibleVerse("Romans", 12, 2, "Do not conform to the pattern of this world, but be transformed by the renewing of your mind. Then you will be able to test and approve what God’s will is—his good, pleasing and perfect will."),
                        BibleVerse("Romans", 12, 12, "Be joyful in hope, patient in affliction, faithful in prayer.")
                    )
                )
            )
        ),
        BibleBook(
            id = "philippians",
            name = "Philippians",
            testament = "New Testament",
            category = "Epistles",
            chapterCount = 4,
            chapters = listOf(
                BibleChapter(
                    bookName = "Philippians",
                    chapterNumber = 4,
                    verses = listOf(
                        BibleVerse("Philippians", 4, 4, "Rejoice in the Lord always. I will say it again: Rejoice!"),
                        BibleVerse("Philippians", 4, 6, "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God."),
                        BibleVerse("Philippians", 4, 7, "And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus."),
                        BibleVerse("Philippians", 4, 13, "I can do all this through him who gives me strength."),
                        BibleVerse("Philippians", 4, 19, "And my God will meet all your needs according to the riches of his glory in Christ Jesus.")
                    )
                )
            )
        )
    )

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
}
