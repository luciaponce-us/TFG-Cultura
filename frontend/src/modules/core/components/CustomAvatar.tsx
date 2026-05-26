import { Avatar, Spinner } from "@chakra-ui/react"

interface CustomAvatarProps extends React.ComponentProps<typeof Avatar.Root> {
  src?: string;
  name: string;
  loading?: boolean;
}

export const CustomAvatar = ({ src, name, loading, ...props }: CustomAvatarProps) => {
  if (loading) {
    return <Avatar.Root {...props}> <Spinner /></Avatar.Root>
  }
  console.log("CustomAvatar render with src:", src);
    return (
    <Avatar.Root {...props}>
      <Avatar.Fallback name={name} />
      <Avatar.Image src={src} />
    </Avatar.Root>
  )
}
