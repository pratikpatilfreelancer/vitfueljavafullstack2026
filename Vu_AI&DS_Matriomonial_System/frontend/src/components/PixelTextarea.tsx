"use client";

interface PixelTextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
}

export function PixelTextarea({ label, className = "", ...props }: PixelTextareaProps) {
  return (
    <div className="pixel-field">
      {label && <label className="pixel-field-label">{label}</label>}
      <textarea className={`pixel-textarea ${className}`} {...props} />
    </div>
  );
}
