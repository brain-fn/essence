(ns db
  (:require [monger.core :as mg]
            [monger.credentials :as mcred]
            [monger.collection :as mc]
            )
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(def default-avatar "https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcR2r781pFCwg_NQnElLIa3EWSundO0otCgBvMH-6tGljpfP9V_Ajw")

 (defn prepopulate-db [db]
   (let [book_Brave (ObjectId.)
         book_Practical (ObjectId.)
         book_Joy (ObjectId.)
         idea_advClo (ObjectId.)
         idea_beginersClo (ObjectId.)
         idea_learnByDoing (ObjectId.)
         idea_HumorAsABonus (ObjectId.)
         idea_CloPhilosophy (ObjectId.)
         idea_TopicClojure (ObjectId.)
         user_Matija (ObjectId.)
         user_SuvashJoy (ObjectId.)
         user_MarshalJoy (ObjectId.)
         user_JohnPract (ObjectId.)
         user_Mukesh (ObjectId.)]

     (mc/insert-batch db "books"
                      [{:_id book_Brave
                        :name "Clojure for the Brave and True"
                        :year 2015
                        :cover "https://d.gr-assets.com/books/1432497082l/20873338.jpg"
                        :authors "Daniel Higginbotham"
                        :goodreads-link "https://www.goodreads.com/book/show/20873338-clojure-for-the-brave-and-true"
                        :impressions [idea_learnByDoing idea_beginersClo idea_HumorAsABonus idea_TopicClojure]}
                       {:_id book_Joy
                        :name "The Joy of Clojure"
                        :year 2011
                        :cover "https://d.gr-assets.com/books/1272940175l/8129142.jpg"
                        :authors "Michael Fogus, Chris Houser"
                        :goodreads-link "https://www.goodreads.com/book/show/8129142-the-joy-of-clojure"
                        :impressions [idea_learnByDoing idea_beginersClo idea_advClo idea_HumorAsABonus idea_CloPhilosophy idea_TopicClojure]}
                       {:_id book_Practical
                        :name "Practical Clojure"
                        :year 2010
                        :cover "https://d.gr-assets.com/books/1347708356l/7903431.jpg"
                        :authors "Luke VanderHart"
                        :goodreads-link "https://www.goodreads.com/book/show/7903431-practical-clojure"
                        :impressions [idea_learnByDoing idea_beginersClo idea_TopicClojure]}
                       ]
                      )

     (mc/insert-batch db "ideas"
                      [{:_id idea_advClo
                        :type :comparable
                        :text "Advanced Clojure"
                        :note "Good for learning advanced Clojure topics"}
                       {:_id idea_beginersClo
                        :type :comparable
                        :text "Beginners Clojure"
                        :note "Good starting point on Clojure"}
                       {:_id idea_learnByDoing
                        :type :comparable
                        :text "Learn by Doing"
                        :note "A lot of excercises to get things right"}
                       {:_id idea_HumorAsABonus
                        :type :comparable
                        :text "Humor Included"
                        :note "While not made for laughs, they may came up"}
                       {:_id idea_CloPhilosophy
                        :type :comparable
                        :text "Clojure Philosophy"
                        :note "Get deep into core principles of Clojure"}
                       {:_id idea_TopicClojure
                        :type :comparable
                        :text "Topic: Clojure"
                        :note "Good example of the topic coverage"}
                       ])

     (mc/insert-batch db "users"
                      [{:_id user_Matija
                        :username "Matija"
                        :userpic default-avatar}
                       {:_id user_SuvashJoy
                        :username "Suvash"
                        :userpic default-avatar}
                       {:_id user_MarshalJoy
                        :username "Marshal"
                        :userpic default-avatar}
                       {:_id user_JohnPract
                        :username "John"
                        :userpic default-avatar}
                       {:_id user_Mukesh
                        :username "Mukesh"
                        :userpic default-avatar}])

     (mc/insert-batch db "impressions"
                      [{:user_id user_Matija
                        :username "Matija"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :rating -2
                        :book book_Joy
                        :opinion "\"I'll say what the book already says about itself pretty vocally, but it's worth repeating: don't read this as your first book on Clojure! It presumes you already know quite a bit about its syntax, and even some of the most common idioms in use without giving even a passing mention. It jumps in right at the deep end of the pool where the table with cocktails is floating - I guess that's where the book derives its name from."
                        }
                       {:_id user_Matija
                        :username "Matija"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :rating 1
                        :book book_Practical
                        :opinion "A dense and solid, yet incomplete intro to the Clojure programming language. Nothing apart from the language overview and some performance considerations is provided, so the title is somewhat misleading - \"Clojure language in a nutshell\" would do it more justice. I found the style terrifically clear and straight-to-the-point, which makes it a good read. On the other hand, important information like destructuring, the reader, etc. has been left out."}
                       {:_id user_SuvashJoy
                        :username "Suvash"
                        :userpic default-avatar
                        :idea_id idea_CloPhilosophy
                        :idea-type :comparable
                        :idea-text "Clojure Philosophy"
                        :idea-note "Get deep into core principles of Clojure"
                        :rating 2
                        :book book_Joy
                        :opinion "Covers a lot, most importantly teaches 'The Clojure way'."}
                       {:_id user_MarshalJoy
                        :username "Marshal"
                        :userpic default-avatar
                        :idea_id idea_TopicClojure
                        :idea-type :comparable
                        :idea-text "Topic: Clojure"
                        :idea-note "Good example of the topic coverage"
                        :rating 2
                        :book book_Joy
                        :opinion "One of the best language-specific programming books I've read in quite a while. "}
                       {:_id user_JohnPract
                        :username "John"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :rating 1
                        :book book_Practical
                        :opinion "Not a bad book, but a little outdated (Clojure 1.1 instead of the most recent 1.7). Definitely an easier read than Clojure in Action. I'd start with this one and use Clojure in Action for a reference."}
                       {:_id user_Mukesh
                        :username "Mukesh"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :rating 2
                        :book book_Brave
                        :opinion "If you want to get into clojure, look no further. This book gives a very good introduction to clojure and the ideas behind the language.\n\nTotally worth it."}])

     ))


 (let [conn (mg/connect-with-credentials "94.237.25.83" 27777 (mcred/create "siteUserAdmin" "admin" "password"))
      db   (mg/get-db conn "essence")]


   ;( prepopulate-db db)


  )