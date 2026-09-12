"use client";

interface PixelButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger" | "teal";
  children: React.ReactNode;
}

export function PixelButton({
  variant = "primary",
  className = "",
  children,
  ...props
}: PixelButtonProps) {
  const variantClass = {
    primary: "pixel-button",
    secondary: "pixel-button-secondary",
    danger: "pixel-button-danger",
    teal: "pixel-lets-begin",
  }[variant];

  return (
    <button className={`${variantClass} ${className}`} {...props}>
      {children}
    </button>
  );
}
