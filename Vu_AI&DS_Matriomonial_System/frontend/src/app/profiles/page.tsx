"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { profileApi, Profile } from "@/lib/api";
import { ProfileCard } from "@/components/ProfileCard";
import { PixelDropdown } from "@/components/PixelDropdown";
import { PixelButton } from "@/components/PixelButton";
import { UserMenu } from "@/components/UserMenu";

export default function ProfilesPage() {
  const router = useRouter();
  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [gender, setGender] = useState("");
  const [city, setCity] = useState("");
  const [age, setAge] = useState("");

  const loadProfiles = async () => {
    setLoading(true);
    try {
      const res = await profileApi.getAll();
      setProfiles(res.data);
    } catch {
      setError("Failed to load profiles");
    }
    setLoading(false);
  };

  useEffect(() => { loadProfiles(); }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const params: any = {};
      if (gender) params.gender = gender;
      if (city) params.city = city;
      if (age) params.age = parseInt(age);
      const res = await profileApi.search(params);
      setProfiles(res.data);
    } catch {
      setError("Search failed");
    }
    setLoading(false);
  };

  return (
    <main className="match-page">
      <UserMenu />
      <div className="match-stage" style={{ display: "block", padding: "2rem clamp(1rem, 4vw, 4rem)" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
          <PixelButton variant="secondary" onClick={() => router.push("/")}>
            ← Back to Catalog
          </PixelButton>
          <h1 className="pixel-page-title" style={{ margin: 0 }}>Browse Profiles</h1>
        </div>

        <div className="pixel-border pixel-card" style={{ marginBottom: "1.5rem" }}>
          <form onSubmit={handleSearch} style={{ display: "flex", flexWrap: "wrap", gap: "0.75rem", alignItems: "flex-end" }}>
            <div style={{ minWidth: 140 }}>
              <PixelDropdown value={gender} onChange={setGender} placeholder="All Genders" options={[
                { value: "Male", label: "Male" },
                { value: "Female", label: "Female" },
              ]} />
            </div>
            <input className="pixel-input" style={{ width: 150 }} placeholder="City" value={city} onChange={e => setCity(e.target.value)} />
            <input className="pixel-input" style={{ width: 100 }} type="number" placeholder="Age" value={age} onChange={e => setAge(e.target.value)} />
            <PixelButton type="submit">Search</PixelButton>
            <PixelButton variant="secondary" type="button" onClick={() => { setGender(""); setCity(""); setAge(""); loadProfiles(); }}>
              Reset
            </PixelButton>
          </form>
        </div>

        {loading && <p style={{ fontFamily: "Press Start 2P", fontSize: "0.6rem", color: "var(--pixel-ink)" }}>Loading...</p>}
        {error && <div className="pixel-alert-error">{error}</div>}
        {!loading && profiles.length === 0 && <p style={{ fontFamily: "Press Start 2P", fontSize: "0.6rem", color: "var(--pixel-ink)" }}>No profiles found.</p>}

        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(340px, 1fr))", gap: "1.5rem" }}>
          {profiles.map(p => <ProfileCard key={p.profileId} profile={p} />)}
        </div>
      </div>
    </main>
  );
}
