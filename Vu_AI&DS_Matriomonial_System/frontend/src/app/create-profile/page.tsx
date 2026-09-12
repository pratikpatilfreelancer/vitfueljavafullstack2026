"use client";

import { useState, useRef } from "react";
import { profileApi, uploadApi } from "@/lib/api";
import { useAuth } from "@/contexts/AuthContext";
import { PixelInput } from "@/components/PixelInput";
import { PixelTextarea } from "@/components/PixelTextarea";
import { PixelDropdown } from "@/components/PixelDropdown";
import { PixelButton } from "@/components/PixelButton";
import Image from "next/image";

const GENDER_OPTIONS = [
  { value: "Male", label: "Male" },
  { value: "Female", label: "Female" },
];

export default function CreateProfilePage() {
  const { user, refreshUser } = useAuth();
  const [age, setAge] = useState("25");
  const [gender, setGender] = useState("Male");
  const [city, setCity] = useState("");
  const [education, setEducation] = useState("");
  const [occupation, setOccupation] = useState("");
  const [about, setAbout] = useState("");
  const [photoUrl, setPhotoUrl] = useState("");
  const [photoFile, setPhotoFile] = useState<File | null>(null);
  const [photoPreview, setPhotoPreview] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

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
    setError("");

    if (!user || !user.userId) {
      setError("User session missing. Please log in again.");
      return;
    }

    setLoading(true);

    try {
      let finalPhotoUrl = photoUrl;

      if (photoFile) {
        const uploadRes = await uploadApi.profilePhoto(photoFile);
        finalPhotoUrl = uploadRes.data.url;
      }

      await profileApi.create(user.userId, {
        age: parseInt(age),
        gender,
        city,
        education,
        occupation,
        about,
        photoUrl: finalPhotoUrl,
      });

      await refreshUser();
    } catch (err: any) {
      setError(err.response?.data?.message || "Failed to create profile");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="match-page">
      <div className="match-stage" style={{ display: "flex", justifyContent: "center", alignItems: "center", minHeight: "100vh", padding: "2rem 1rem" }}>
        <div className="pixel-border pixel-card" style={{ maxWidth: 540, width: "100%", background: "var(--pixel-pink-soft)" }}>
          <div style={{ textAlign: "center", marginBottom: "1.5rem" }}>
            <h1 className="pixel-page-title" style={{ fontSize: "1.2rem", color: "var(--pixel-pink)" }}>
              ♥ Heartmate Profile Setup
            </h1>
            <p style={{ fontFamily: "'Press Start 2P', monospace", fontSize: "0.5rem", color: "var(--pixel-ink)", lineHeight: 1.6 }}>
              WELCOME {user?.name?.toUpperCase() || "USER"}! CREATE YOUR CANDIDATE PROFILE TO BEGIN MATCHING.
            </p>
          </div>

          {error && <div className="pixel-alert-error">{error}</div>}

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
                    onClick={() => { setPhotoFile(null); setPhotoPreview(""); }}
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
              placeholder="e.g. Mumbai, Delhi, Bangalore"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              required
            />

            <PixelInput
              label="EDUCATION"
              placeholder="e.g. B.Tech CSE, MBA Finance"
              value={education}
              onChange={(e) => setEducation(e.target.value)}
            />

            <PixelInput
              label="OCCUPATION"
              placeholder="e.g. Software Developer, Architect"
              value={occupation}
              onChange={(e) => setOccupation(e.target.value)}
            />

            <PixelTextarea
              label="ABOUT YOURSELF"
              placeholder="Tell prospective matches a little bit about yourself, hobbies, and interests..."
              value={about}
              onChange={(e) => setAbout(e.target.value)}
              rows={3}
            />

            <div style={{ marginTop: "1.5rem" }}>
              <PixelButton type="submit" disabled={loading} style={{ width: "100%" }}>
                {loading ? "SAVING PROFILE..." : "COMPLETE PROFILE & START MATCHING ♥"}
              </PixelButton>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}
