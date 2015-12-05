(ns essence.core)

(enable-console-print!)

(set! (.-innerHTML (.getElementById js/document "app"))
      "Hello, world!")
