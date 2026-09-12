import { Composition } from "remotion";
import { WalkthroughSequence } from "./WalkthroughSequence";

export const RemotionRoot = () => {
  return (
    <>
      <Composition
        id="HeartMateWalkthrough"
        component={WalkthroughSequence}
        durationInFrames={1200} // 40 seconds at 30fps
        fps={30}
        width={1920}
        height={1080}
      />
    </>
  );
};
