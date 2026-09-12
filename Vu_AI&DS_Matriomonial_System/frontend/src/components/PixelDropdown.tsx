"use client";

import { useState, useRef, useEffect } from "react";

interface PixelDropdownProps {
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
  placeholder: string;
  "aria-label"?: string;
}

export function PixelDropdown({
  value,
  onChange,
  options,
  placeholder,
  "aria-label": ariaLabel,
}: PixelDropdownProps) {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  const selected = options.find((o) => o.value === value);

  return (
    <div className="pixel-dropdown" ref={ref} aria-label={ariaLabel}>
      <button
        type="button"
        className="pixel-select pixel-dropdown-trigger"
        onClick={() => setOpen(!open)}
        aria-haspopup="listbox"
        aria-expanded={open}
      >
        <span className="pixel-dropdown-value">
          {selected ? selected.label : placeholder}
        </span>
        <svg
          className={`pixel-dropdown-arrow ${open ? "open" : ""}`}
          xmlns="http://www.w3.org/2000/svg"
          width="10"
          height="10"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="4"
          strokeLinecap="square"
          strokeLinejoin="miter"
        >
          <path d="m6 9 6 6 6-6" />
        </svg>
      </button>
      {open && (
        <ul className="pixel-dropdown-menu" role="listbox">
          {options.map((opt) => (
            <li
              key={opt.value}
              className={`pixel-dropdown-item ${opt.value === value ? "selected" : ""}`}
              role="option"
              aria-selected={opt.value === value}
              onClick={() => {
                onChange(opt.value);
                setOpen(false);
              }}
            >
              {opt.label}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
