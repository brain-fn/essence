 (ns db)
 
(ns mongotests
  (:require [monger.core :as mg]
            [monger.credentials :as mcred]
            [monger.collection :as mc]
            )
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))


(let [conn (mg/connect-with-credentials "94.237.25.83" 27777 (mcred/create "siteUserAdmin" "admin" "password"))
      db   (mg/get-db conn "essence")]





  )