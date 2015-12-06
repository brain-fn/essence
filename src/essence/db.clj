(ns essence.db
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.credentials :as mcred]
            [monger.collection :as mc]
            [monger.query :refer :all]
            [monger.operators :refer :all]
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
                        :book_id book_Joy
                        :book-name "The Joy of Clojure"
                        :book-year 2011
                        :book-cover "https://d.gr-assets.com/books/1272940175l/8129142.jpg"
                        :rating -2
                        :opinion "\"I'll say what the book already says about itself pretty vocally, but it's worth repeating: don't read this as your first book on Clojure! It presumes you already know quite a bit about its syntax, and even some of the most common idioms in use without giving even a passing mention. It jumps in right at the deep end of the pool where the table with cocktails is floating - I guess that's where the book derives its name from."
                        :datetime (java.util.Date.)
                        }
                       {:user_id user_JohnPract
                          :username "John"
                          :userpic default-avatar
                          :idea_id idea_beginersClo
                          :idea-type :comparable
                          :idea-text "Beginners Clojure"
                          :idea-note "Good starting point on Clojure"
                          :book_id book_Joy
                          :book-name "The Joy of Clojure"
                          :book-year 2011
                          :book-cover "https://d.gr-assets.com/books/1272940175l/8129142.jpg"
                          :rating -2
                          :opinion "JoC may not be a good first book to read for a Clojure newbie. It's deep like the language itself and it will take you a while to fully appreciate the elegance of what you encounter. I for one know that I'll be back to re-read sections that didn't fully sink in the first time around."
                          :datetime (java.util.Date.)
                          }
                       {:user_id user_Matija
                        :username "Matija"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :book_id book_Practical
                        :book-name "Practical Clojure"
                        :book-year 2010
                        :book-cover "https://d.gr-assets.com/books/1347708356l/7903431.jpg"
                        :rating 1
                        :opinion "A dense and solid, yet incomplete intro to the Clojure programming language. Nothing apart from the language overview and some performance considerations is provided, so the title is somewhat misleading - \"Clojure language in a nutshell\" would do it more justice. I found the style terrifically clear and straight-to-the-point, which makes it a good read. On the other hand, important information like destructuring, the reader, etc. has been left out."
                        :datetime (java.util.Date.)}
                       {:user_id user_SuvashJoy
                        :username "Suvash"
                        :userpic default-avatar
                        :idea_id idea_CloPhilosophy
                        :idea-type :comparable
                        :idea-text "Clojure Philosophy"
                        :idea-note "Get deep into core principles of Clojure"
                        :book_id book_Joy
                        :book-name "The Joy of Clojure"
                        :book-year 2011
                        :book-cover "https://d.gr-assets.com/books/1272940175l/8129142.jpg"
                        :rating 2
                        :opinion "Covers a lot, most importantly teaches 'The Clojure way'."
                        :datetime (java.util.Date.)}
                       {:user_id user_MarshalJoy
                        :username "Marshal"
                        :userpic default-avatar
                        :idea_id idea_TopicClojure
                        :idea-type :comparable
                        :idea-text "Topic: Clojure"
                        :idea-note "Good example of the topic coverage"
                        :book_id book_Joy
                        :book-name "The Joy of Clojure"
                        :book-year 2011
                        :book-cover "https://d.gr-assets.com/books/1272940175l/8129142.jpg"
                        :rating 2
                        :opinion "One of the best language-specific programming books I've read in quite a while. "
                        :datetime (java.util.Date.)}
                       {:user_id user_JohnPract
                        :username "John"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :book_id book_Practical
                        :book-name "Practical Clojure"
                        :book-year 2010
                        :book-cover "https://d.gr-assets.com/books/1347708356l/7903431.jpg"
                        :rating 1
                        :opinion "Not a bad book, but a little outdated (Clojure 1.1 instead of the most recent 1.7). Definitely an easier read than Clojure in Action. I'd start with this one and use Clojure in Action for a reference."
                        :datetime (java.util.Date.)}
                       {:user_id user_Mukesh
                        :username "Mukesh"
                        :userpic default-avatar
                        :idea_id idea_beginersClo
                        :idea-type :comparable
                        :idea-text "Beginners Clojure"
                        :idea-note "Good starting point on Clojure"
                        :book_id book_Brave
                        :book-name "Clojure for the Brave and True"
                        :book-year 2015
                        :book-cover "https://d.gr-assets.com/books/1432497082l/20873338.jpg"
                        :rating 2
                        :opinion "If you want to get into clojure, look no further. This book gives a very good introduction to clojure and the ideas behind the language.\n\nTotally worth it."
                        :datetime (java.util.Date.)}])

     ))


 (def conn (mg/connect-with-credentials "94.237.25.83" 27777 (mcred/create "siteUserAdmin" "admin" "password")))
 (def db   (mg/get-db conn "essence"))


(defn map-function-on-map-vals [m f]
  (apply merge
         (map (fn [[k v]] {k (f v)})
              m)))

(defn str_id [doc]
  (map-function-on-map-vals doc #(if (= (type %) ObjectId) (str %) %))
  )

;  Users

(defn get-user-data [user_id]
  (str_id
    (mc/find-one-as-map db "users" {:_id (ObjectId. user_id)}))
  )

(defn get-user-from-name [username]
  (str_id (mc/find-one-as-map db "users" {:username username}))
  )

(defn add-user
  ([username] (add-user username default-avatar))
  ([username avatar]
   (str_id
     (mc/insert db "users" {:username username
                            :userpic avatar}))))


;  Ideas

(defn get-idea-data [idea_id]
  (str_id (mc/find-one-as-map db "ideas" {:_id (ObjectId. idea_id)}))
  )

(defn get-idea-impressions [idea_id]
  (str_id (mc/find-maps db "impressions" {:idea_id (ObjectId. idea_id)})))

(defn add-idea [type text note]
  (str_id (mc/insert db "ideas" {:type type
                                 :text text
                                 :note note})) )


;  Books

(defn get-book-data [book_id]
  (str_id (mc/find-one-as-map db "books" {:_id (ObjectId. book_id)}))
  )

(defn add-book [name year cover_url authors goodreads-link]
  (str_id (mc/insert db "books" {:name name
                         :year year
                         :cover cover_url
                         :authors authors
                         :goodreads-link goodreads-link})))

(defn list-books []
  (map str_id ( mc/find-maps db "books"))
  )

  (defn can-rate?
    ([username impression_id]
     (if (nil? username) nil
                         (empty? (mc/find-one db "impressions" {:_id (ObjectId. impression_id)
                                                                :username username}))
                         ))
    ([username book_id idea_id]
      (if (nil? username) nil
                       (empty? (mc/find-one db "impressions" {:book_id (ObjectId. book_id)
                                                              :idea_id (ObjectId. idea_id)
                                                              :username username})))))

(defn get-book-ideas
  ([book_id] (get-book-ideas book_id nil))
  ([book_id username]
    (map (fn [[_id imprs]]
         {:idea_id _id
          :idea-text (:idea-text (first imprs))
          :idea-note (:idea-note (first imprs))
          :impressions-count (count imprs)
          :impressions imprs
          :user-can-rate (can-rate? username (:book_id (first imprs)) _id)})
    (group-by :idea_id
      (map str_id (mc/find-maps db "impressions" {:idea-type :idea
                                                  :book_id (ObjectId. book_id)
                                        } [:rating :idea-text :idea-note :idea_id :book_id]))))))

(defn wrap-comps [username [_id comps]]
  {:idea_id _id
   :idea-text (:idea-text (first comps))
   :idea-note (:idea-note (first comps))
   :idea-rating (reduce + (map :rating comps) )
   :impressions-count (count comps)
   :impressions comps
   :user-can-rate (can-rate? username (:book_id (first comps)) _id)})

(defn sort-comps [comps]
  (sort-by :idea-rating comps ))

(defn get-book-good-for
  ([book_id] (get-book-good-for book_id nil ) )
  ([book_id username]
  (sort-comps
      (map (partial wrap-comps username)
           (group-by :idea_id
                     (map str_id (mc/find-maps db "impressions" {:idea-type :comparable
                                                                 :book_id (ObjectId. book_id)
                                                                 :rating { $gte 0}}
                                               [:rating :idea-text :idea-note :idea_id  :book_id])))
                    )
               )))
(defn get-book-bad-for
  ([book_id] (get-book-bad-for book_id nil ) )
  ([book_id username]
  ;(reverse
    (sort-comps
      (map (partial wrap-comps username)
           (group-by :idea_id
                     (map str_id (mc/find-maps db "impressions" {:idea-type :comparable
                                                                 :book_id   (ObjectId. book_id)
                                                                 :rating    {$lte 0}}
                                               [:rating :idea-text :idea-note :idea_id :book_id])))
           )
      )
  ; )
  ))

;  Impressions

(defn get-impression-data [impression_id]
  (str_id (mc/find-one-as-map db "impressions" {:_id (ObjectId. impression_id)})))

(defn add-impression [user_id idea_id book_id rating opinion]
  (let [user (get-user-data user_id)
        idea (get-idea-data idea_id)
        book (get-book-data book_id)]
    (str_id (mc/insert db "impressions"
                {:user_id (:_id user)
                 :username (:username user)
                 :userpic (:userpic user)
                 :idea_id (:_id idea)
                 :idea-type (:idea-type idea)
                 :idea-text (:idea-text idea)
                 :idea-note (:idea-note idea)
                 :book_id (:_id book)
                 :book-name (:book-name book)
                 :book-year (:book-year book)
                 :book-cover (:book-cover book)
                 :rating rating
                 :opinion opinion
                 :datetime (java.util.Date.)
                 }))))

(defn prop-impression [user_id impression_id prop-sign]
  (let [impression (get-impression-data impression_id)
        user (get-user-data user_id)]
    (if (can-rate? (:username user) impression_id)
      (mc/insert db "impressions" (assoc impression
                                    :username (:username user)
                                    :user_id (:_id user)
                                    :userpic (:userpic user)
                                    :opinion nil
                                    :rating (if (pos? prop-sign) 1 -1)
                                    :datetime (java.util.Date.)))
      )))

;  Views

(defn get-book-impressions [book_id]
  (map str_id
    (with-collection db "impressions"
                   (find {:book_id (ObjectId. book_id)})
                   (sort {:datetime -1}))))

(defn get-book-insight
  ([book_id] (get-book-insight book_id nil))
  ([book_id username]
  ; some representation of ideas that are in the book
  (assoc (get-book-data book_id )
    :ideas (get-book-ideas book_id username)
    :good-for (get-book-good-for book_id username)
    :bad-for (get-book-bad-for book_id username)
  )))

(defn get-idea [idea_id]
  (assoc (get-idea-data idea_id) :impressions
                                 (get-idea-impressions idea_id))
  )



;(get-book-insight "5662ee29505e7c5d71a9aba6")
; (in-ns 'essence.db) (require '[clojure.pprint :refer [pprint]])
; (def idea "5662ee29505e7c5d71a9aba9") (def book_Brave "5662ee29505e7c5d71a9aba5")
