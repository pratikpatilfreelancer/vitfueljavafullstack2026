"use client";

interface PixelInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export function PixelInput({ label, className = "", ...props }: PixelInputProps) {
  return (
    <div className="pixel-field">
      {label && <label className="pixel-field-label">{label}</label>}
      <input className={`pixel-input ${className}`} {...props} />
    </div>
  );
}
