"use client";

import { Profile } from "@/lib/api";
import { PixelButton } from "@/components/PixelButton";
import { PixelAvatar } from "@/components/PixelAvatar";
import { useRouter } from "next/navigation";

export function ProfileCard({ 
  profile, 
  showActionButton = true, 
  actionLabel = "View Details",
  onAction 
}: { 
  profile: Profile; 
  showActionButton?: boolean; 
  actionLabel?: string;
  onAction?: () => void;
}) {
  const router = useRouter();

  const handleAction = () => {
    if (onAction) {
      onAction();
    } else {
      router.push(`/profiles/${profile.profileId}`);
    }
  };

  return (
    <div className="pixel-border pixel-card profile-card">
      <PixelAvatar gender={profile.gender} photoUrl={profile.photoUrl} name={profile.user?.name} />
      <div className="profile-card-content">
        <h3 className="profile-card-name">
          {profile.user?.name || "Unknown"}
        </h3>
        <div className="profile-card-meta">
          <p>{profile.gender} - {profile.age} yrs - {profile.city}</p>
          <p>{profile.occupation || "Occupation N/A"}</p>
          {profile.about && (
            <p className="profile-card-about">
              {profile.about}
            </p>
          )}
        </div>
        {showActionButton && (
          <div className="profile-card-action">
            <PixelButton onClick={handleAction}>
              {actionLabel}
            </PixelButton>
          </div>
        )}
      </div>
    </div>
  );
}
