import CalculateIcon from "@mui/icons-material/Calculate";
import { Button, IconButton } from "@mui/material";

import LargerTooltip from "./LargerTooltip";

interface ComputeButtonProps {
  title?: string;
  onClick: () => void;
  iconButton?: boolean;
}

export default function ComputeButton({
  title = "Compute",
  onClick,
  iconButton = true,
}: ComputeButtonProps) {
  return iconButton ? (
    <LargerTooltip title={title}>
      <IconButton onClick={onClick} color="primary">
        <CalculateIcon />
      </IconButton>
    </LargerTooltip>
  ) : (
    <Button
      onClick={onClick}
      variant="outlined"
      // startIcon={<CalculateIcon />}
      sx={{ m: 1 }}
    >
      {title}
    </Button>
  );
}
