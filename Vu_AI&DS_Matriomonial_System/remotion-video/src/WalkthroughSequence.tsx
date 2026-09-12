import React from "react";
import {
  AbsoluteFill,
  Sequence,
  interpolate,
  spring,
  useCurrentFrame,
  useVideoConfig,
  Img,
} from "remotion";

export const WalkthroughSequence: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  // Helper springs
  const springIn = (delayFrames: number) =>
    spring({
      frame: frame - delayFrames,
      fps,
      config: { damping: 12 },
    });

  // Scene timing (30 fps)
  // Scene 1: Intro & Landing Page + Cupid Physics (Frames 0 - 240)
  // Scene 2: Candidate Profiles & Side-by-Side Match Page (Frames 240 - 540)
  // Scene 3: Match Celebration (Confetti, Heart, Cats) (Frames 540 - 810)
  // Scene 4: Manage Interests (DB records) (Frames 810 - 1020)
  // Scene 5: Admin Panel & Swagger Docs (Frames 1020 - 1200)

  return (
    <AbsoluteFill
      style={{
        backgroundColor: "#ffd6e2",
        fontFamily: "'Press Start 2P', monospace, sans-serif",
        color: "#3d2626",
        overflow: "hidden",
      }}
    >
      {/* Background Pixel Pattern */}
      <div
        style={{
          position: "absolute",
          inset: 0,
          opacity: 0.15,
          backgroundImage: "linear-gradient(#0000 50%, #ffffff38 50%)",
          backgroundSize: "100% 12px",
        }}
      />

      {/* Header Banner across all scenes */}
      <div
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          right: 0,
          height: "90px",
          background: "#fff5ee",
          borderBottom: "6px solid #e8658a",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "0 50px",
          zIndex: 100,
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "20px" }}>
          <span style={{ fontSize: "36px", color: "#e8658a" }}>♥</span>
          <span style={{ fontSize: "28px", color: "#3d2626" }}>HEARTMATE</span>
        </div>
        <div
          style={{
            fontSize: "20px",
            color: "#c9486b",
            background: "#ffd6e2",
            padding: "10px 20px",
            border: "4px solid #e8658a",
            borderRadius: "8px",
          }}
        >
          {frame < 240 && "Scene 1: Interactive Pixel Landing"}
          {frame >= 240 && frame < 540 && "Scene 2: Side-by-Side Match Comparison"}
          {frame >= 540 && frame < 810 && "Scene 3: Match Celebration & Chroma Key Cats"}
          {frame >= 810 && frame < 1020 && "Scene 4: Real-time DB Interest Management"}
          {frame >= 1020 && "Scene 5: Admin Panel & Swagger Open API"}
        </div>
      </div>

      {/* SCENE 1: LANDING & CUPID PHYSICS (0 - 240) */}
      <Sequence from={0} durationInFrames={240}>
        <SceneLanding frame={frame} fps={fps} />
      </Sequence>

      {/* SCENE 2: SIDE-BY-SIDE MATCH PAGE (240 - 540) */}
      <Sequence from={240} durationInFrames={300}>
        <SceneSideBySide frame={frame - 240} fps={fps} />
      </Sequence>

      {/* SCENE 3: MATCH CELEBRATION (540 - 810) */}
      <Sequence from={540} durationInFrames={270}>
        <SceneMatchCelebration frame={frame - 540} fps={fps} />
      </Sequence>

      {/* SCENE 4: MANAGE INTERESTS (810 - 1020) */}
      <Sequence from={810} durationInFrames={210}>
        <SceneInterests frame={frame - 810} fps={fps} />
      </Sequence>

      {/* SCENE 5: ADMIN & SWAGGER DOCS (1020 - 1200) */}
      <Sequence from={1020} durationInFrames={180}>
        <SceneAdminSwagger frame={frame - 1020} fps={fps} />
      </Sequence>
    </AbsoluteFill>
  );
};

// ==================== SCENE 1: LANDING PAGE ====================
const SceneLanding: React.FC<{ frame: number; fps: number }> = ({ frame, fps }) => {
  // Cupid spring displacement simulation
  const cupidPull = frame > 90 && frame < 160 ? Math.sin((frame - 90) * 0.15) * 60 : 0;

  return (
    <div style={{ width: "100%", height: "100%", position: "relative", paddingTop: "140px" }}>
      {/* Title */}
      <div style={{ textAlign: "center", marginTop: "40px" }}>
        <h1 style={{ fontSize: "56px", color: "#3d2626", textShadow: "4px 4px 0 #fff5ee" }}>
          Find Your <span style={{ color: "#e8658a" }}>Perfect Match</span>
        </h1>
        <p style={{ fontSize: "20px", marginTop: "20px", color: "#3d2626", lineHeight: "1.8" }}>
          Join thousands of happy souls who found their forever.
        </p>
      </div>

      {/* Form Card */}
      <div
        style={{
          width: "700px",
          margin: "40px auto 0",
          background: "#fff5ee",
          border: "6px solid #e8658a",
          boxShadow: "10px 10px 0 #b34d6e",
          padding: "35px",
          borderRadius: "16px",
        }}
      >
        <p style={{ fontSize: "20px", color: "#e8658a", textAlign: "center", marginBottom: "25px" }}>
          ♥ SEARCH CANDIDATES ♥
        </p>
        <div style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "15px", fontSize: "18px" }}>
            <span>I'm looking for a:</span>
            <div style={{ padding: "12px 20px", border: "4px solid #e8658a", background: "#fff", color: "#c9486b" }}>
              {frame > 40 ? "Woman" : "Select"}
            </div>
            <span>aged</span>
            <div style={{ padding: "12px 20px", border: "4px solid #e8658a", background: "#fff" }}>22</div>
            <span>to</span>
            <div style={{ padding: "12px 20px", border: "4px solid #e8658a", background: "#fff" }}>27</div>
          </div>
          <button
            style={{
              padding: "20px",
              background: frame > 180 ? "#1a9aab" : "#2cb5c8",
              color: "#fff",
              fontSize: "22px",
              border: "4px solid #1a9aab",
              boxShadow: "0 6px 0 #147a87",
              transform: frame > 180 ? "translate(3px, 3px)" : "none",
            }}
          >
            {frame > 180 ? "Searching Profiles..." : "Let's Begin"}
          </button>
        </div>
      </div>

      {/* Left Physics Cupid */}
      <div
        style={{
          position: "absolute",
          left: "80px",
          bottom: "120px",
          transform: `translateY(${cupidPull}px)`,
        }}
      >
        <div style={{ width: "4px", height: "400px", borderLeft: "4px dashed #c9486b", margin: "0 auto" }} />
        <div style={{ fontSize: "90px", transform: "scaleX(-1)" }}>🏹</div>
      </div>

      {/* Right Cupid */}
      <div style={{ position: "absolute", right: "80px", bottom: "120px" }}>
        <div style={{ width: "4px", height: "400px", borderLeft: "4px dashed #c9486b", margin: "0 auto" }} />
        <div style={{ fontSize: "90px" }}>🏹</div>
      </div>
    </div>
  );
};

// ==================== SCENE 2: SIDE-BY-SIDE MATCH ====================
const SceneSideBySide: React.FC<{ frame: number; fps: number }> = ({ frame, fps }) => {
  const showModal = frame > 120 && frame < 220;
  const showMail = frame > 240;

  return (
    <div style={{ width: "100%", height: "100%", paddingTop: "140px", position: "relative" }}>
      <h2 style={{ textAlign: "center", fontSize: "32px", color: "#c9486b", marginBottom: "40px" }}>
        Profile Details Comparison
      </h2>

      {/* Side by Side Cards */}
      <div style={{ display: "flex", justifyContent: "center", alignItems: "center", gap: "60px" }}>
        {/* YOU Card */}
        <div
          style={{
            width: "400px",
            background: "#fff5ee",
            border: "6px solid #e8658a",
            boxShadow: "8px 8px 0 #b34d6e",
            padding: "30px",
            borderRadius: "16px",
          }}
        >
          <p style={{ fontSize: "20px", color: "#e8658a", textAlign: "center", marginBottom: "15px" }}>YOU</p>
          <div style={{ display: "flex", gap: "20px", alignItems: "center" }}>
            <div style={{ fontSize: "60px", background: "#ffe6b0", border: "4px solid #3d2626", borderRadius: "12px", padding: "10px" }}>
              👦
            </div>
            <div>
              <h3 style={{ fontSize: "22px", color: "#c9486b" }}>Sriyaan</h3>
              <p style={{ fontSize: "14px", marginTop: "8px" }}>Male · 24 yrs · Delhi</p>
              <p style={{ fontSize: "14px", marginTop: "4px", color: "#e8658a" }}>Software Developer</p>
            </div>
          </div>
        </div>

        <div style={{ fontSize: "48px", color: "#e8658a" }}>♥</div>

        {/* THEM Card */}
        <div
          style={{
            width: "400px",
            background: "#fff5ee",
            border: "6px solid #e8658a",
            boxShadow: "8px 8px 0 #b34d6e",
            padding: "30px",
            borderRadius: "16px",
          }}
        >
          <p style={{ fontSize: "20px", color: "#e8658a", textAlign: "center", marginBottom: "15px" }}>THEM</p>
          <div style={{ display: "flex", gap: "20px", alignItems: "center" }}>
            <div style={{ fontSize: "60px", background: "#ffe6b0", border: "4px solid #3d2626", borderRadius: "12px", padding: "10px" }}>
              👧
            </div>
            <div>
              <h3 style={{ fontSize: "22px", color: "#c9486b" }}>Priya Patel</h3>
              <p style={{ fontSize: "14px", marginTop: "8px" }}>Female · 25 yrs · Mumbai</p>
              <p style={{ fontSize: "14px", marginTop: "4px", color: "#e8658a" }}>Financial Analyst</p>
            </div>
          </div>
          <button
            style={{
              width: "100%",
              marginTop: "20px",
              padding: "12px",
              background: "#e8658a",
              color: "#fff",
              fontSize: "16px",
              border: "3px solid #c9486b",
            }}
          >
            View Details
          </button>
        </div>
      </div>

      {/* Send Interest Button */}
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        <button
          style={{
            padding: "22px 50px",
            background: frame > 200 ? "#27ae60" : "#e8658a",
            color: "#fff",
            fontSize: "24px",
            border: "5px solid #c9486b",
            boxShadow: "0 8px 0 #b34d6e",
          }}
        >
          {frame > 200 ? "Interest Sent! Waiting for Response..." : "Send Interest"}
        </button>
      </div>

      {/* Bouncing Love Letter Pop-up */}
      {showMail && (
        <div
          style={{
            position: "absolute",
            top: "120px",
            right: "80px",
            fontSize: "72px",
            animation: "bounce 0.5s infinite alternate",
            cursor: "pointer",
          }}
        >
          💌
        </div>
      )}

      {/* Modal Popup */}
      {showModal && (
        <div
          style={{
            position: "absolute",
            inset: 0,
            background: "rgba(0,0,0,0.7)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 200,
          }}
        >
          <div style={{ background: "#fff", padding: "40px", border: "6px solid #e8658a", borderRadius: "16px", width: "500px" }}>
            <h3 style={{ fontSize: "22px", color: "#e8658a", marginBottom: "20px" }}>Priya Patel's Full Profile</h3>
            <p style={{ fontSize: "16px", lineHeight: "1.8" }}>
              <strong>Education:</strong> MBA Finance<br />
              <strong>Occupation:</strong> Financial Analyst<br />
              <strong>Email:</strong> priya@example.com<br />
              <strong>About:</strong> Foodie at heart. Love exploring cafes and painting.
            </p>
          </div>
        </div>
      )}
    </div>
  );
};

// ==================== SCENE 3: MATCH CELEBRATION ====================
const SceneMatchCelebration: React.FC<{ frame: number; fps: number }> = ({ frame, fps }) => {
  return (
    <div style={{ width: "100%", height: "100%", paddingTop: "140px", textAlign: "center", position: "relative" }}>
      <h2 style={{ fontSize: "48px", color: "#ff007f", textShadow: "4px 4px 0 #fff" }}>
        IT'S A MATCH! ♥
      </h2>

      {/* Big Popping Heart */}
      <div style={{ fontSize: "120px", margin: "20px 0", animation: "pulse 1s infinite alternate", color: "#ff007f" }}>
        ♥
      </div>

      <p style={{ fontSize: "24px", color: "#3d2626" }}>Sriyaan & Priya Patel matched!</p>

      {/* Confetti Particles */}
      {Array.from({ length: 30 }).map((_, i) => (
        <div
          key={i}
          style={{
            position: "absolute",
            top: `${(i * 35) % 800 + 100}px`,
            left: `${(i * 65) % 1800 + 50}px`,
            width: "18px",
            height: "18px",
            background: i % 2 === 0 ? "#ff007f" : "#ff7eb3",
            transform: `rotate(${frame * 5 + i * 20}deg)`,
          }}
        />
      ))}

      {/* Chroma Key Dancing Cats Video Simulation */}
      <div
        style={{
          position: "absolute",
          bottom: "30px",
          left: "50%",
          transform: "translateX(-50%)",
          display: "flex",
          gap: "30px",
          alignItems: "flex-end",
        }}
      >
        <div style={{ fontSize: "90px" }}>🐱</div>
        <div style={{ fontSize: "110px" }}>🐱</div>
        <div style={{ fontSize: "90px" }}>🐱</div>
      </div>
    </div>
  );
};

// ==================== SCENE 4: MANAGE INTERESTS ====================
const SceneInterests: React.FC<{ frame: number; fps: number }> = ({ frame, fps }) => {
  return (
    <div style={{ width: "100%", height: "100%", paddingTop: "140px", paddingLeft: "100px", paddingRight: "100px" }}>
      <h2 style={{ fontSize: "36px", color: "#c9486b", marginBottom: "30px" }}>
        Manage Interests (MySQL Real-time Storage)
      </h2>

      {/* Tabs */}
      <div style={{ display: "flex", gap: "20px", marginBottom: "30px" }}>
        <button style={{ padding: "15px 40px", background: "#e8658a", color: "#fff", fontSize: "20px", border: "4px solid #c9486b" }}>
          Sent (1)
        </button>
        <button style={{ padding: "15px 40px", background: "#fff5ee", color: "#3d2626", fontSize: "20px", border: "4px solid #e8658a" }}>
          Received (0)
        </button>
      </div>

      {/* Interest Record Card */}
      <div
        style={{
          background: "#fff",
          border: "5px solid #e8658a",
          boxShadow: "6px 6px 0 #b34d6e",
          padding: "30px",
          borderRadius: "16px",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "25px" }}>
          <div style={{ fontSize: "50px" }}>👧</div>
          <div>
            <h3 style={{ fontSize: "24px", color: "#c9486b" }}>Priya Patel</h3>
            <p style={{ fontSize: "16px", marginTop: "6px", color: "#666" }}>priya@example.com</p>
            <span
              style={{
                display: "inline-block",
                marginTop: "10px",
                padding: "8px 16px",
                background: "#27ae6026",
                border: "2px solid #27ae60",
                color: "#27ae60",
                fontSize: "14px",
              }}
            >
              STATUS: ACCEPTED
            </span>
          </div>
        </div>
        <button style={{ padding: "12px 25px", background: "#e74c3c", color: "#fff", fontSize: "16px", border: "3px solid #c0392b" }}>
          Remove Record
        </button>
      </div>
    </div>
  );
};

// ==================== SCENE 5: ADMIN & SWAGGER DOCS ====================
const SceneAdminSwagger: React.FC<{ frame: number; fps: number }> = ({ frame, fps }) => {
  return (
    <div style={{ width: "100%", height: "100%", paddingTop: "140px", paddingLeft: "100px", paddingRight: "100px" }}>
      <h2 style={{ fontSize: "36px", color: "#c9486b", marginBottom: "30px" }}>
        Admin Dashboard & Swagger OpenAPI 3.0
      </h2>

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "40px" }}>
        {/* Admin Card */}
        <div style={{ background: "#fff", border: "5px solid #e8658a", padding: "25px", borderRadius: "16px" }}>
          <h3 style={{ fontSize: "20px", color: "#e8658a", marginBottom: "15px" }}>System Database Controls</h3>
          <p style={{ fontSize: "16px", lineHeight: "2" }}>
            ✔ Total Registered Users: 12<br />
            ✔ Active Matrimonial Profiles: 12<br />
            ✔ Connected DB: MySQL (matrimonial_jpa_db)<br />
            ✔ REST Controller Endpoints: 17
          </p>
        </div>

        {/* Swagger OpenAPI Card */}
        <div style={{ background: "#89bf04", color: "#fff", border: "5px solid #6b9602", padding: "25px", borderRadius: "16px" }}>
          <h3 style={{ fontSize: "20px", color: "#fff", marginBottom: "15px" }}>Swagger UI Documentation 📖</h3>
          <p style={{ fontSize: "16px", lineHeight: "2", color: "#fff" }}>
            http://localhost:8080/swagger-ui/index.html<br />
            - GET /api/users<br />
            - POST /api/profiles/user/{'{userId}'}<br />
            - POST /api/interests/send
          </p>
        </div>
      </div>
    </div>
  );
};
