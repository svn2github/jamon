;;; jamon-java.el ---

; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

;;; Commentary:

;; This file contains the definition of utility functions for
;; navigating to Jamon source template files from the generated java
;; files.

;;; Code:

;;{{{

(defun jj-jump-to-source ()
  "From within a generated Jamon template implementation, jump to
  the corresponding Jamon template source file and line."
  (interactive)
  (if
      (catch 'not-jamon-impl
        (let ((template-file (jj-find-template-file-name))
              (position (jj-find-closest-position)))
          (find-file template-file)
          (beginning-of-buffer)
          (forward-line (- (car position) 1))
          (forward-char (- (cdr position) 1))
          ))
    (error "Not a generated Jamon file")))

(defun jj-in-generated-source-p ()
  (interactive)
  (save-excursion
    (beginning-of-buffer)
    (looking-at "// Autogenerated Jamon \\(implementation\\|proxy\\)$")))


(defun jj-find-template-file-name ()
  ""
  (save-excursion
    (beginning-of-buffer)
    (if (not (jj-in-generated-source-p))
        (throw 'not-jamon-impl 1)
      (forward-line 1)
      (if (not (looking-at "^// \\(.*\\)$"))
          (throw 'not-jamon-impl 2)
        (buffer-substring-no-properties (match-beginning 1) (match-end 1))
        )
      )
    )
  )

(defun jj-parse-int (m)
  (string-to-int
   (buffer-substring-no-properties (match-beginning m) (match-end m))))

(defconst jj-position-regexp "^ *// \\([0-9]+\\), \\([0-9]+\\)$")

(defun jj-closest-position (d)
  ""
  (beginning-of-line)
  (while (and
          (not (looking-at jj-position-regexp))
          (= (forward-line d) 0)))
  (if (looking-at jj-position-regexp)
      (cons (jj-parse-int 1) (jj-parse-int 2))))

(defun jj-find-closest-position ()
  ""
  (save-excursion
    (let ((p (point)))
      (or (jj-closest-position -1)
          (progn (goto-char p) (jj-closest-position 1))
          '(1 . 1)))))

(defvar jj-error-in-process nil)

(defun jj-previous-error ()
  (interactive)
  (cond (jj-error-in-process
         (switch-to-buffer (cddr jj-error-in-process))
         (goto-char (car jj-error-in-process))
         (setq jj-error-in-process nil)
         )
        (t (previous-error))))

(defun jj-next-error (&optional ARGP)
  (interactive "P")
  (cond ((and (not ARGP) (not jj-error-in-process) (jj-in-generated-source-p))
         (setq jj-error-in-process
               (cons (point) (cons (mark t) (current-buffer))))
         (jj-jump-to-source))
        (t
         (setq jj-error-in-process nil)
         (next-error ARGP))))

(provide 'jamon-java)

;;}}}
