"use client";

import { useState, useEffect, Suspense, useRef } from "react";
import { useRouter } from "next/navigation";
import { profileApi, uploadApi } from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";
import { PixelInput } from "@/components/PixelInput";
import { PixelTextarea } from "@/components/PixelTextarea";
import { PixelDropdown } from "@/components/PixelDropdown";
import { PixelButton } from "@/components/PixelButton";
import { UserMenu } from "@/components/UserMenu";
import Image from "next/image";

const GENDER_OPTIONS = [
  { value: "Male", label: "Male" },
  { value: "Female", label: "Female" },
];

function EditProfileContent() {
  const router = useRouter();
  const { user, profile, refreshUser, logout } = useAuth();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [age, setAge] = useState("");
  const [gender, setGender] = useState("Male");
  const [city, setCity] = useState("");
  const [education, setEducation] = useState("");
  const [occupation, setOccupation] = useState("");
  const [about, setAbout] = useState("");
  const [photoUrl, setPhotoUrl] = useState("");
  const [photoFile, setPhotoFile] = useState<File | null>(null);
  const [photoPreview, setPhotoPreview] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (profile) {
      setAge(String(profile.age || ""));
      setGender(profile.gender || "Male");
      setCity(profile.city || "");
      setEducation(profile.education || "");
      setOccupation(profile.occupation || "");
      setAbout(profile.about || "");
      setPhotoUrl(profile.photoUrl || "");
      setPhotoPreview(profile.photoUrl || "");
    }
  }, [profile]);

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setError("Please select an image file");
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setError("Image must be under 5MB");
      return;
    }

    setPhotoFile(file);
    setError("");

    const reader = new FileReader();
    reader.onload = (ev) => setPhotoPreview(ev.target?.result as string);
    reader.readAsDataURL(file);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage("");
    setError("");

    if (!user || !user.userId) {
      setError("User session missing.");
      return;
    }

    setLoading(true);

    try {
      let finalPhotoUrl = photoUrl;

      if (photoFile) {
        const uploadRes = await uploadApi.profilePhoto(photoFile);
        finalPhotoUrl = uploadRes.data.url;
      }

      const data = {
        age: parseInt(age),
        gender,
        city,
        education,
        occupation,
        about,
        photoUrl: finalPhotoUrl,
      };

      if (profile && profile.profileId) {
        await profileApi.update(profile.profileId, data);
        setMessage("Profile updated successfully!");
      } else {
        await profileApi.create(user.userId, data);
        setMessage("Profile created successfully!");
      }

      await refreshUser();
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to save profile");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!profile || !profile.profileId || !confirm("Are you sure you want to delete your profile?")) return;
    try {
      await profileApi.delete(profile.profileId);
      await logout();
    } catch {
      setError("Failed to delete profile");
    }
  };

  return (
    <main className="match-page" style={{ minHeight: "100vh" }}>
      <UserMenu />
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)", maxWidth: "700px", margin: "0 auto" }}>
        
        {/* Navigation Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
          <PixelButton variant="secondary" onClick={() => router.push("/")}>
            ← Back to Catalog
          </PixelButton>
          <h1 className="pixel-page-title" style={{ margin: 0, fontSize: "1.1rem" }}>
            Edit Candidate Profile
          </h1>
        </div>

        <div className="pixel-border pixel-card" style={{ background: "var(--pixel-pink-soft)" }}>
          {message && <div className="pixel-alert-success">{message}</div>}
          {error && <div className="pixel-alert-error">{error}</div>}

          <div style={{ marginBottom: "1rem", padding: "0.6rem", background: "rgba(255,255,255,0.4)", borderRadius: "4px" }}>
            <p style={{ fontFamily: "Press Start 2P", fontSize: "0.5rem", color: "var(--pixel-ink)", margin: 0 }}>
              ACCOUNT: <strong>{user?.name}</strong> ({user?.email})
            </p>
          </div>

          <form onSubmit={handleSubmit}>
            {/* Photo Upload */}
            <div style={{ marginBottom: "1.5rem" }}>
              <label className="pixel-field-label" style={{ display: "block", marginBottom: "0.5rem" }}>PROFILE PHOTO</label>
              <div
                onClick={() => fileInputRef.current?.click()}
                style={{
                  width: 140,
                  height: 140,
                  margin: "0 auto",
                  border: "3px dashed var(--pixel-pink)",
                  borderRadius: 8,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  cursor: "pointer",
                  overflow: "hidden",
                  background: "rgba(255,255,255,0.5)",
                  transition: "border-color 0.2s",
                }}
              >
                {photoPreview ? (
                  <Image
                    src={photoPreview}
                    alt="Photo preview"
                    width={140}
                    height={140}
                    style={{ objectFit: "cover" }}
                    unoptimized
                  />
                ) : (
                  <div style={{ textAlign: "center", fontFamily: "Press Start 2P", fontSize: "0.45rem", color: "var(--pixel-pink)", padding: "0.5rem" }}>
                    📷<br />CLICK TO<br />ADD PHOTO
                  </div>
                )}
              </div>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handlePhotoChange}
                style={{ display: "none" }}
              />
              {photoPreview && (
                <div style={{ textAlign: "center", marginTop: "0.5rem" }}>
                  <PixelButton
                    type="button"
                    variant="secondary"
                    onClick={() => { setPhotoFile(null); setPhotoUrl(""); setPhotoPreview(""); }}
                    style={{ fontSize: "0.4rem", padding: "0.3rem 0.6rem" }}
                  >
                    REMOVE PHOTO
                  </PixelButton>
                </div>
              )}
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
              <PixelInput
                label="AGE"
                type="number"
                min="18"
                max="99"
                value={age}
                onChange={(e) => setAge(e.target.value)}
                required
              />
              <div className="pixel-field">
                <label className="pixel-field-label">GENDER</label>
                <PixelDropdown
                  placeholder="Gender"
                  value={gender}
                  onChange={setGender}
                  options={GENDER_OPTIONS}
                />
              </div>
            </div>

            <PixelInput
              label="CITY"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              required
            />

            <PixelInput
              label="EDUCATION"
              value={education}
              onChange={(e) => setEducation(e.target.value)}
            />

            <PixelInput
              label="OCCUPATION"
              value={occupation}
              onChange={(e) => setOccupation(e.target.value)}
            />

            <PixelTextarea
              label="ABOUT"
              value={about}
              onChange={(e) => setAbout(e.target.value)}
              rows={4}
            />

            <div style={{ marginTop: "1.5rem", display: "flex", gap: "1rem" }}>
              <PixelButton type="submit" disabled={loading} style={{ flex: 1 }}>
                {loading ? "SAVING..." : profile ? "UPDATE PROFILE" : "CREATE PROFILE"}
              </PixelButton>
              {profile && (
                <PixelButton variant="danger" type="button" onClick={handleDelete}>
                  DELETE
                </PixelButton>
              )}
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}

export default function EditProfilePage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <EditProfileContent />
    </Suspense>
  );
}
