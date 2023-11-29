# video_operations.py
from moviepy.editor import VideoFileClip, vfx, AudioFileClip
import sys
import json


def get_duration(input_file):
    video_clip = VideoFileClip(input_file)
    return video_clip.duration


def trim_video(input_file, output_file, start_time, end_time):
    video_clip = VideoFileClip(input_file)
    trimmed_clip = video_clip.subclip(start_time, end_time)
    trimmed_clip.write_videofile(output_file, codec="libx264", audio_codec="aac")


def replace_audio(input_video, input_audio, output_file):
    video_clip = VideoFileClip(input_video)
    audio_clip = AudioFileClip(input_audio)
    video_clip = video_clip.set_audio(audio_clip)
    video_clip.write_videofile(output_file, codec="libx264", audio_codec="aac")


def change_speed(input_file, output_file, speed_factor=2.0):
    video_clip = VideoFileClip(input_file)
    sped_up_clip = video_clip.fx(vfx.speedx, speed_factor)
    sped_up_clip.write_videofile(output_file, codec="libx264", audio_codec="aac")


def rotate_video(input_file, output_file, angle=90):
    video_clip = VideoFileClip(input_file)
    rotated_clip = video_clip.fx(vfx.rotate, angle)
    rotated_clip.write_videofile(output_file, codec="libx264", audio_codec="aac")


if __name__ == "__main__":
    # Parse command-line arguments
    operation = sys.argv[1]

    if operation == "get_duration":
        input_file = sys.argv[2]
        result = get_duration(input_file)

    elif operation == "trim_video":
        input_file, output_file, start_time, end_time = sys.argv[2], sys.argv[3], float(sys.argv[4]), float(sys.argv[5])
        trim_video(input_file, output_file, start_time, end_time)
        result = "Trimmed video successfully."

    elif operation == "replace_audio":
        input_video, input_audio, output_file = sys.argv[2], sys.argv[3], sys.argv[4]
        replace_audio(input_video, input_audio, output_file)
        result = "Replaced audio successfully."

    elif operation == "change_speed":
        input_file, output_file, speed_factor = sys.argv[2], sys.argv[3], float(sys.argv[4])
        change_speed(input_file, output_file, speed_factor)
        result = "Changed speed successfully."

    elif operation == "rotate_video":
        input_file, output_file, angle = sys.argv[2], sys.argv[3], float(sys.argv[4])
        rotate_video(input_file, output_file, angle)
        result = "Rotated video successfully."

    else:
        result = "Invalid operation."

    # Print the result as JSON for easy parsing in Java
    print(result)
