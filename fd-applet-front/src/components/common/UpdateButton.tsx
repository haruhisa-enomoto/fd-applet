import SyncIcon from "@mui/icons-material/Sync";
import Button from "@mui/material/Button";

interface UpdateButtonProps {
  title?: string;
  onClick: () => void;
}

export default function UpdateButton({
  title = "Get Data",
  onClick,
}: UpdateButtonProps) {
  return (
    <Button
      onClick={onClick}
      variant="outlined"
      startIcon={<SyncIcon />}
      sx={{ m: 1 }}
    >
      {title}
    </Button>
  );
}
