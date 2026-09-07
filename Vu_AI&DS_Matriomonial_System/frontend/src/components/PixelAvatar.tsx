"use client";

import Image from "next/image";

type PixelAvatarProps = {
  gender?: string;
  photoUrl?: string;
  name?: string;
  size?: "card" | "large";
};

export function PixelAvatar({ gender, photoUrl, name, size = "card" }: PixelAvatarProps) {
  const variant = gender === "Female" ? "girl" : "boy";

  return (
    <div className={`pixel-avatar pixel-avatar-${size}`}>
      {photoUrl ? (
        <Image
          className="pixel-avatar-image"
          src={photoUrl}
          alt={name || "Profile photo"}
          width={64}
          height={64}
          unoptimized
        />
      ) : (
        <Image
          className="pixel-avatar-image"
          src={`/avatar-${variant}.png`}
          alt={variant === "girl" ? "Girl avatar" : "Boy avatar"}
          width={64}
          height={64}
        />
      )}
    </div>
  );
}
