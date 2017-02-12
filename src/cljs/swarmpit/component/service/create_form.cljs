(ns swarmpit.component.service.create-form
  (:require [swarmpit.material :as material]
            [rum.core :as rum]))

(enable-console-print!)

(rum/defc form-text < rum/static []
  [:p "For each ad campaign that you create, you can control how much you're willing to spend
           on clicks and conversions, which networks and geographical locations you want\n your ads to
           show on, and more.\n"])

(rum/defc form < rum/reactive []
  [:div.form
   (material/theme
     (material/stepper #js {:activeStep 1}
                       (material/step #js {}
                                      (material/step-label #js {} "Select campaign settings"))
                       (material/step #js {}
                                      (material/step-label #js {} "Create an ad group"))
                       (material/step #js {}
                                      (material/step-label #js {} "Create an ad"))))
   (form-text)
   [:div.form-buttons
    (material/theme
      (material/flat-button #js {:label    "Back"
                                 :disabled true
                                 :style    #js {:marginRight "12px"}}))
    (material/theme
      (material/raised-button #js {:label   "Start"
                                   :primary true}))]])

(defn mount!
  []
  (rum/mount (form) (.getElementById js/document "content")))
