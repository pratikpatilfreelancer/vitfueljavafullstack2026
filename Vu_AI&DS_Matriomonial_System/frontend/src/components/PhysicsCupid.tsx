"use client";

import React, { useState, useEffect, useRef } from "react";
import Image from "next/image";

interface PhysicsCupidProps {
  side: "left" | "right";
}

export function PhysicsCupid({ side }: PhysicsCupidProps) {
  const containerRef = useRef<HTMLDivElement>(null);

  // Physics state for Cupid displacement & string pluck
  const [isDragging, setIsDragging] = useState(false);
  const [isHovered, setIsHovered] = useState(false);

  // Positions relative to rest position
  const posRef = useRef({ x: 0, y: 0 }); // Cupid offset
  const velRef = useRef({ vx: 0, vy: 0 }); // Cupid velocity

  // Control point offset for the string curve (physics pluck)
  const stringCtrlRef = useRef({ x: 0, y: 0 });
  const stringVelRef = useRef({ vx: 0, vy: 0 });

  const dragStartRef = useRef({ x: 0, y: 0 });
  const [renderPos, setRenderPos] = useState({ x: 0, y: 0, stringCx: 0, stringCy: 0 });

  // Floating hover animation time
  const timeRef = useRef(side === "left" ? 0 : 1.7);

  useEffect(() => {
    let animId: number;

    const updatePhysics = () => {
      timeRef.current += 0.03;
      // Gentle natural float offset
      const floatY = Math.sin(timeRef.current) * 12;
      const floatRot = Math.cos(timeRef.current * 0.8) * 2;

      if (!isDragging) {
        // Spring physics for Cupid position return to (0, floatY)
        const targetX = 0;
        const targetY = floatY;

        const k = 0.08; // Stiffness
        const damping = 0.82; // Friction

        const ax = (targetX - posRef.current.x) * k;
        const ay = (targetY - posRef.current.y) * k;

        velRef.current.vx = (velRef.current.vx + ax) * damping;
        velRef.current.vy = (velRef.current.vy + ay) * damping;

        posRef.current.x += velRef.current.vx;
        posRef.current.y += velRef.current.vy;

        // String control point spring physics (pluck vibration)
        const stringTargetX = posRef.current.x * 0.5;
        const stringTargetY = posRef.current.y * 0.5;

        const sk = 0.12;
        const sdamping = 0.78;

        const sax = (stringTargetX - stringCtrlRef.current.x) * sk;
        const say = (stringTargetY - stringCtrlRef.current.y) * sk;

        stringVelRef.current.vx = (stringVelRef.current.vx + sax) * sdamping;
        stringVelRef.current.vy = (stringVelRef.current.vy + say) * sdamping;

        stringCtrlRef.current.x += stringVelRef.current.vx;
        stringCtrlRef.current.y += stringVelRef.current.vy;
      }

      setRenderPos({
        x: posRef.current.x,
        y: posRef.current.y + (isDragging ? 0 : floatY),
        stringCx: stringCtrlRef.current.x,
        stringCy: stringCtrlRef.current.y,
      });

      animId = requestAnimationFrame(updatePhysics);
    };

    updatePhysics();
    return () => cancelAnimationFrame(animId);
  }, [isDragging, side]);

  // Mouse / Touch handlers for dragging
  const handleMouseDown = (e: React.MouseEvent | React.TouchEvent) => {
    setIsDragging(true);
    const clientX = "touches" in e ? e.touches[0].clientX : e.clientX;
    const clientY = "touches" in e ? e.touches[0].clientY : e.clientY;
    dragStartRef.current = {
      x: clientX - posRef.current.x,
      y: clientY - posRef.current.y,
    };
  };

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent | TouchEvent) => {
      if (!isDragging) return;
      const clientX = "touches" in e ? e.touches[0].clientX : e.clientX;
      const clientY = "touches" in e ? e.touches[0].clientY : e.clientY;

      const newX = clientX - dragStartRef.current.x;
      const newY = clientY - dragStartRef.current.y;

      // Limit max pull distance
      const maxDist = 180;
      const dist = Math.hypot(newX, newY);
      if (dist > maxDist) {
        posRef.current.x = (newX / dist) * maxDist;
        posRef.current.y = (newY / dist) * maxDist;
      } else {
        posRef.current.x = newX;
        posRef.current.y = newY;
      }

      // String bulges out when pulled
      stringCtrlRef.current.x = posRef.current.x * 0.7;
      stringCtrlRef.current.y = posRef.current.y * 0.7;
    };

    const handleMouseUp = () => {
      if (isDragging) {
        setIsDragging(false);
        // Give string extra pluck impulse on release
        stringVelRef.current.vx = (Math.random() - 0.5) * 30;
        stringVelRef.current.vy = (Math.random() - 0.5) * 30;
      }
    };

    if (isDragging) {
      window.addEventListener("mousemove", handleMouseMove);
      window.addEventListener("mouseup", handleMouseUp);
      window.addEventListener("touchmove", handleMouseMove);
      window.addEventListener("touchend", handleMouseUp);
    }
    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
      window.removeEventListener("touchmove", handleMouseMove);
      window.removeEventListener("touchend", handleMouseUp);
    };
  }, [isDragging]);

  // Calculations for SVG string curve
  const cupidWidth = 240;
  const cupidHeight = 240;
  // SVG String Calculations
  // SVG covers area from top of screen down to Cupid
  const stringAnchorX = 120; // Center of Cupid container
  const stringAnchorY = -600; // Top anchor (well above screen)
  const attachX = 120 + renderPos.x;
  const attachY = 40 + renderPos.y; // Cupid head attach point

  const controlX = (stringAnchorX + attachX) / 2 + renderPos.stringCx;
  const controlY = (stringAnchorY + attachY) / 2 + renderPos.stringCy;

  const stringPath = `M ${stringAnchorX} ${stringAnchorY} Q ${controlX} ${controlY} ${attachX} ${attachY}`;

  return (
    <div
      ref={containerRef}
      className={`cupid-physics-wrapper cupid-${side}`}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      style={{
        position: "absolute",
        zIndex: 20,
        cursor: isDragging ? "grabbing" : "grab",
        userSelect: "none",
        touchAction: "none",
        filter: isHovered ? "drop-shadow(0 0 10px rgba(255, 105, 180, 0.8))" : "none",
        transition: "filter 0.2s ease",
      }}
    >
      {/* Interactive Cupid Image with Original CSS String */}
      <div
        onMouseDown={handleMouseDown}
        onTouchStart={handleMouseDown}
        style={{
          transform: `translate(${renderPos.x}px, ${renderPos.y}px) rotate(${renderPos.x * 0.15}deg) scale(${isDragging ? 1.08 : isHovered ? 1.04 : 1})`,
          transition: isDragging ? "none" : "transform 0.08s ease-out",
          width: "100%",
          display: "flex",
          justifyContent: "center",
          position: "relative",
        }}
      >
        {/* Original String Element */}
        <div className="cupid-string" aria-hidden="true" />
        
        <Image
          className="pixel-cupid"
          src="/pixel-cupid.png"
          alt="Pixel Cupid"
          width={816}
          height={816}
          draggable={false}
          style={{
            transform: side === "left" ? "scaleX(-1)" : "none",
            pointerEvents: "auto",
          }}
        />
      </div>
    </div>
  );
}
