(ns savings-tracker.util.presentation)

(defn currency
  [number]
  (.format (js/numeral number) "$0,0[.]00"))
